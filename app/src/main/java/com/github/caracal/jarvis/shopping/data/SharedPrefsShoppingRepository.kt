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
 */
class SharedPrefsShoppingRepository(context: Context) : ShoppingRepository {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val allItems: MutableList<ShoppingItem> = mutableListOf()

    init {
        loadFromPrefs()
        ensureBaselineItems()
    }

    override fun getShoppingList(): List<ShoppingItem> = 
        allItems.filter { it.isOnShoppingList }.sortedWith(itemComparator)

    override fun getReplenishList(): List<ShoppingItem> =
        allItems.filter { !it.isOnShoppingList }.sortedWith(itemComparator)

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

    override fun addShoppingItemWithBarcode(name: String, categoryId: String, barcode: String): Boolean {
        val trimmed = name.trim()
        val existing = allItems.find {
            it.name.equals(trimmed, ignoreCase = true) && it.categoryId == categoryId
        }
        
        if (existing != null) {
            val index = allItems.indexOf(existing)
            val updatedBarcodes = (existing.barcodes + barcode).distinct()
            allItems[index] = existing.copy(isOnShoppingList = true, barcodes = updatedBarcodes)
        } else {
            val newItem = ShoppingItem(
                id = java.util.UUID.randomUUID().toString(),
                name = trimmed,
                categoryId = categoryId,
                isBaseline = false,
                barcodes = listOf(barcode),
                isOnShoppingList = true
            )
            allItems.add(newItem)
        }
        saveToPrefs()
        return true
    }

    override fun addShoppingItem(name: String, categoryId: String): ShoppingItem? {
        val trimmed = name.trim()
        val existing = allItems.find {
            it.name.equals(trimmed, ignoreCase = true) && it.categoryId == categoryId
        }

        if (existing != null) {
            // If it's already on the shopping list, we treat it as a duplicate error.
            if (existing.isOnShoppingList) {
                return null
            }
            val index = allItems.indexOf(existing)
            val updated = existing.copy(isOnShoppingList = true)
            allItems[index] = updated
            saveToPrefs()
            return updated
        }

        val newItem = ShoppingItem(
            id = java.util.UUID.randomUUID().toString(),
            name = trimmed,
            categoryId = categoryId,
            isBaseline = false,
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
            // "Soft delete": keep the item but mark it as not on the shopping list
            // so it stays in the replenish list.
            allItems[index] = allItems[index].copy(isOnShoppingList = false)
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
        if (changed) saveToPrefs()
    }

    private fun loadFromPrefs() {
        allItems.clear()
        val json = prefs.getString(KEY_SHOPPING_LIST, null) ?: return
        try {
            val array = JSONArray(json)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val barcodes = mutableListOf<String>()
                val barcodesArray = obj.optJSONArray(FIELD_BARCODES)
                if (barcodesArray != null) {
                    for (j in 0 until barcodesArray.length()) {
                        barcodes.add(barcodesArray.getString(j))
                    }
                }
                allItems.add(
                    ShoppingItem(
                        id = obj.getString(FIELD_ID),
                        name = obj.getString(FIELD_NAME),
                        categoryId = obj.getString(FIELD_CATEGORY_ID),
                        isBaseline = obj.optBoolean(FIELD_IS_BASELINE, false),
                        barcodes = barcodes,
                        isOnShoppingList = obj.optBoolean(FIELD_IS_ON_SHOPPING_LIST, true)
                    )
                )
            }
        } catch (_: Exception) {
            allItems.clear()
        }
    }

    private fun saveToPrefs() {
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
        prefs.edit { putString(KEY_SHOPPING_LIST, array.toString()) }
    }

    companion object {
        private const val PREFS_NAME = "shopping_prefs"
        private const val KEY_SHOPPING_LIST = "shopping_list_v1"
        private const val FIELD_ID = "id"
        private const val FIELD_NAME = "name"
        private const val FIELD_CATEGORY_ID = "category_id"
        private const val FIELD_IS_BASELINE = "is_baseline"
        private const val FIELD_BARCODES = "barcodes"
        private const val FIELD_IS_ON_SHOPPING_LIST = "is_on_shopping_list"

        private val itemComparator: Comparator<ShoppingItem> = compareBy(
            { BaselineData.categoryById(it.categoryId)?.name?.lowercase() ?: "" },
            { it.name.lowercase() }
        )
    }
}
