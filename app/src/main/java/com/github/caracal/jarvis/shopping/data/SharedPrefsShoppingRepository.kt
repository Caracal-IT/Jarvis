package com.github.caracal.jarvis.shopping.data

import android.content.Context
import androidx.core.content.edit
import org.json.JSONArray
import org.json.JSONObject

/**
 * SharedPreferences-backed implementation of [ShoppingRepository].
 *
 * Persists the Shopping List across app and device restarts. The Replenish List
 * includes both predefined [BaselineData] items and user-added items that are
 * not currently on the shopping list.
 *
 * @param context Application context used to access SharedPreferences.
 * @param syncPublisher Optional publisher notified of locally-originated state changes, for
 *   cloud sync. Not invoked when a remote snapshot is applied via [applyRemoteSnapshot].
 */
class SharedPrefsShoppingRepository(
    context: Context,
    private val syncPublisher: ShoppingSyncPublisher? = null
) : ShoppingRepository {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val allItems: MutableList<ShoppingItem> = mutableListOf()
    private val changeListeners: MutableList<() -> Unit> = mutableListOf()
    private var lastModified: Long = 0L

    init {
        loadFromPrefs()
        ensureBaselineItems()
    }

    override fun getShoppingList(): List<ShoppingItem> = 
        allItems.filter { it.isOnShoppingList }.sortedWith(itemComparator)

    override fun getReplenishList(): List<ShoppingItem> =
        allItems.filter { !it.isOnShoppingList || it.isBaseline }.sortedWith(itemComparator)

    override fun getCategories(): List<ShoppingCategory> =
        BaselineData.categories.sortedBy { it.name }

    override fun updateShoppingItem(
        itemId: String,
        newName: String,
        newCategoryId: String,
        newBarcodes: List<String>
    ): Boolean {
        val index = allItems.indexOfFirst { it.id == itemId }
        if (index < 0) return false
        val trimmed = newName.trim()
        val duplicate = allItems.any {
            it.id != itemId &&
                it.name.equals(trimmed, ignoreCase = true) &&
                it.categoryId == newCategoryId
        }
        if (duplicate) return false
        allItems[index] = allItems[index].copy(
            name = trimmed,
            categoryId = newCategoryId,
            barcodes = newBarcodes
        )
        saveToPrefs()
        return true
    }

    override fun findByBarcode(barcode: String): ShoppingItem? =
        allItems.find { barcode in it.barcodes }

    override fun addShoppingItemWithBarcode(
        name: String,
        categoryId: String,
        barcode: String,
        isBaseline: Boolean
    ): Boolean {
        val trimmed = name.trim()
        val existing = allItems.find {
            it.name.equals(trimmed, ignoreCase = true) && it.categoryId == categoryId
        }
        
        if (existing != null) {
            val index = allItems.indexOf(existing)
            val updatedBarcodes = (existing.barcodes + barcode).distinct()
            allItems[index] = existing.copy(
                isOnShoppingList = true, 
                barcodes = updatedBarcodes,
                isBaseline = existing.isBaseline || isBaseline
            )
        } else {
            val newItem = ShoppingItem(
                id = java.util.UUID.randomUUID().toString(),
                name = trimmed,
                categoryId = categoryId,
                isBaseline = isBaseline,
                barcodes = listOf(barcode),
                isOnShoppingList = true
            )
            allItems.add(newItem)
        }
        saveToPrefs()
        return true
    }

    override fun addShoppingItem(name: String, categoryId: String, isBaseline: Boolean): ShoppingItem {
        val trimmed = name.trim()
        val existing = allItems.find {
            it.name.equals(trimmed, ignoreCase = true) && it.categoryId == categoryId
        }

        if (existing != null) {
            val index = allItems.indexOf(existing)
            val updated = existing.copy(
                isOnShoppingList = true,
                isBaseline = existing.isBaseline || isBaseline
            )
            allItems[index] = updated
            saveToPrefs()
            return updated
        }

        val newItem = ShoppingItem(
            id = java.util.UUID.randomUUID().toString(),
            name = trimmed,
            categoryId = categoryId,
            isBaseline = isBaseline,
            barcodes = emptyList(),
            isOnShoppingList = true
        )
        allItems.add(newItem)
        saveToPrefs()
        return newItem
    }

    override fun renameShoppingItem(itemId: String, newName: String): Boolean {
        val index = allItems.indexOfFirst { it.id == itemId }
        if (index < 0) return false
        val item = allItems[index]
        val trimmed = newName.trim()
        val duplicate = allItems.any {
            it.id != itemId &&
                it.name.equals(trimmed, ignoreCase = true) &&
                it.categoryId == item.categoryId
        }
        if (duplicate) return false
        allItems[index] = item.copy(name = trimmed)
        saveToPrefs()
        return true
    }

    override fun removeShoppingItem(itemId: String) {
        val index = allItems.indexOfFirst { it.id == itemId }
        if (index >= 0) {
            val item = allItems[index]
            if (item.isBaseline) {
                // Keep it but mark as not on list.
                allItems[index] = item.copy(isOnShoppingList = false)
            } else {
                // Permanently remove non-baseline items.
                allItems.removeAt(index)
            }
            saveToPrefs()
        }
    }

    override fun addBarcode(itemId: String, barcode: String) {
        val index = allItems.indexOfFirst { it.id == itemId }
        if (index < 0) return
        val item = allItems[index]
        if (barcode !in item.barcodes) {
            allItems[index] = item.copy(barcodes = item.barcodes + barcode)
            saveToPrefs()
        }
    }

    override fun removeBarcode(itemId: String, barcode: String) {
        val index = allItems.indexOfFirst { it.id == itemId }
        if (index < 0) return
        val item = allItems[index]
        allItems[index] = item.copy(barcodes = item.barcodes - barcode)
        saveToPrefs()
    }

    override fun addBaselineItemToShoppingList(baselineItemId: String): Boolean {
        val index = allItems.indexOfFirst { it.id == baselineItemId }
        if (index < 0) return false
        val item = allItems[index]
        if (item.isOnShoppingList) return false
        
        allItems[index] = item.copy(isOnShoppingList = true)
        saveToPrefs()
        return true
    }

    override fun addChangeListener(listener: () -> Unit) {
        changeListeners.add(listener)
    }

    override fun exportSnapshot(): String = buildStateJson().toString()

    override fun applyRemoteSnapshot(json: String): Boolean {
        val remoteState = try {
            JSONObject(json)
        } catch (e: Exception) {
            android.util.Log.e(TAG, "Failed to parse remote snapshot; ignoring.", e)
            return false
        }
        val remoteTimestamp = remoteState.optLong(FIELD_TIMESTAMP, -1L)
        if (remoteTimestamp <= lastModified) return false

        val remoteItems = parseItems(remoteState.optJSONArray(FIELD_ITEMS))
        allItems.clear()
        allItems.addAll(remoteItems)
        lastModified = remoteTimestamp
        persistCurrentState()
        notifyChangeListeners()
        return true
    }

    // ── Private helpers ──────────────────────────────────────────────────────

    private fun ensureBaselineItems() {
        var changed = false
        for (baseline in BaselineData.baselineItems) {
            if (allItems.none { it.id == baseline.id }) {
                // If it's a baseline item not yet in our list, add it as NOT on shopping list.
                allItems.add(baseline.copy(isOnShoppingList = false))
                changed = true
            }
        }
        // Persist without bumping lastModified/publishing: this is default local population, not
        // a user edit, and must not out-rank a not-yet-fetched but genuinely newer cloud snapshot.
        if (changed) persistCurrentState()
    }

    private fun loadFromPrefs() {
        allItems.clear()
        lastModified = prefs.getLong(KEY_LAST_MODIFIED, 0L)
        val json = prefs.getString(KEY_SHOPPING_LIST, null) ?: return
        val array = try {
            JSONArray(json)
        } catch (e: Exception) {
            android.util.Log.e(TAG, "Failed to parse persisted shopping list; resetting to empty.", e)
            return
        }
        allItems.addAll(parseItems(array))
    }

    /** Parses a [FIELD_ITEMS]-shaped [JSONArray] into [ShoppingItem]s, skipping malformed records. */
    private fun parseItems(array: JSONArray?): List<ShoppingItem> {
        if (array == null) return emptyList()
        val result = mutableListOf<ShoppingItem>()
        for (i in 0 until array.length()) {
            try {
                val obj = array.getJSONObject(i)
                val barcodes = mutableListOf<String>()
                val barcodesArray = obj.optJSONArray(FIELD_BARCODES)
                if (barcodesArray != null) {
                    for (j in 0 until barcodesArray.length()) {
                        barcodes.add(barcodesArray.getString(j))
                    }
                }
                result.add(
                    ShoppingItem(
                        id = obj.getString(FIELD_ID),
                        name = obj.getString(FIELD_NAME),
                        categoryId = obj.getString(FIELD_CATEGORY_ID),
                        isBaseline = obj.optBoolean(FIELD_IS_BASELINE, false),
                        barcodes = barcodes,
                        isOnShoppingList = obj.optBoolean(FIELD_IS_ON_SHOPPING_LIST, true)
                    )
                )
            } catch (e: Exception) {
                // Skip only the malformed record; don't discard the rest of the list.
                android.util.Log.e(TAG, "Skipping malformed shopping item at index $i.", e)
            }
        }
        return result
    }

    /** Builds the `{timestamp, items}` JSON representation of the current in-memory state. */
    private fun buildStateJson(): JSONObject {
        val array = JSONArray()
        for (item in allItems) {
            val obj = JSONObject()
            obj.put(FIELD_ID, item.id)
            obj.put(FIELD_NAME, item.name)
            obj.put(FIELD_CATEGORY_ID, item.categoryId)
            obj.put(FIELD_IS_BASELINE, item.isBaseline)
            obj.put(FIELD_IS_ON_SHOPPING_LIST, item.isOnShoppingList)
            val barcodes = JSONArray()
            item.barcodes.forEach { barcodes.put(it) }
            obj.put(FIELD_BARCODES, barcodes)
            array.put(obj)
        }
        return JSONObject().apply {
            put(FIELD_TIMESTAMP, lastModified)
            put(FIELD_ITEMS, array)
        }
    }

    /** Writes the current in-memory state and [lastModified] to SharedPreferences. */
    private fun persistCurrentState() {
        val state = buildStateJson()
        prefs.edit {
            putString(KEY_SHOPPING_LIST, state.getJSONArray(FIELD_ITEMS).toString())
            putLong(KEY_LAST_MODIFIED, lastModified)
        }
    }

    private fun notifyChangeListeners() {
        changeListeners.toList().forEach { it() }
    }

    /** Persists a locally-originated change: bumps [lastModified], saves, notifies, and publishes. */
    private fun saveToPrefs() {
        lastModified = System.currentTimeMillis()
        persistCurrentState()
        notifyChangeListeners()
        syncPublisher?.publish(exportSnapshot())
    }

    companion object {
        private const val TAG = "SharedPrefsShoppingRepository"
        private const val PREFS_NAME = "shopping_prefs"
        private const val KEY_SHOPPING_LIST = "shopping_list_v1"
        private const val KEY_LAST_MODIFIED = "shopping_list_last_modified_v1"
        private const val FIELD_ID = "id"
        private const val FIELD_NAME = "name"
        private const val FIELD_CATEGORY_ID = "category_id"
        private const val FIELD_IS_BASELINE = "is_baseline"
        private const val FIELD_BARCODES = "barcodes"
        private const val FIELD_IS_ON_SHOPPING_LIST = "is_on_shopping_list"
        private const val FIELD_TIMESTAMP = "timestamp"
        private const val FIELD_ITEMS = "items"

        private val itemComparator: Comparator<ShoppingItem> = compareBy(
            { BaselineData.categoryById(it.categoryId)?.name?.lowercase() ?: "" },
            { it.name.lowercase() }
        )
    }
}
