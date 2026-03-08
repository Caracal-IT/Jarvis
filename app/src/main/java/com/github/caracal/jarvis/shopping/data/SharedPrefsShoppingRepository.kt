package com.github.caracal.jarvis.shopping.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

/**
 * SharedPreferences-backed implementation of [ShoppingRepository].
 *
 * Persists the Shopping List across app and device restarts. The Replenish List
 * is derived from [BaselineData] and does not require separate persistence.
 *
 * @param context Application context used to access SharedPreferences.
 */
class SharedPrefsShoppingRepository(context: Context) : ShoppingRepository {

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val shoppingList: MutableList<ShoppingItem> = mutableListOf()

    init {
        loadFromPrefs()
    }

    override fun getShoppingList(): List<ShoppingItem> = shoppingList.sortedWith(itemComparator)

    override fun getReplenishList(): List<ShoppingItem> =
        BaselineData.baselineItems.sortedWith(itemComparator)

    override fun getCategories(): List<ShoppingCategory> =
        BaselineData.categories.sortedBy { it.name }

    override fun addShoppingItem(name: String, categoryId: String): ShoppingItem? {
        val trimmed = name.trim()
        val duplicate = shoppingList.any {
            it.name.equals(trimmed, ignoreCase = true) && it.categoryId == categoryId
        }
        if (duplicate) return null

        val newItem = ShoppingItem(
            id = java.util.UUID.randomUUID().toString(),
            name = trimmed,
            categoryId = categoryId,
            isBaseline = false,
            barcodes = emptyList()
        )
        shoppingList.add(newItem)
        saveToPrefs()
        return newItem
    }

    override fun renameShoppingItem(itemId: String, newName: String): Boolean {
        val index = shoppingList.indexOfFirst { it.id == itemId }
        if (index < 0) return false
        val item = shoppingList[index]
        val trimmed = newName.trim()
        val duplicate = shoppingList.any {
            it.id != itemId &&
                it.name.equals(trimmed, ignoreCase = true) &&
                it.categoryId == item.categoryId
        }
        if (duplicate) return false
        shoppingList[index] = item.copy(name = trimmed)
        saveToPrefs()
        return true
    }

    override fun removeShoppingItem(itemId: String) {
        shoppingList.removeAll { it.id == itemId }
        saveToPrefs()
    }

    override fun addBarcode(itemId: String, barcode: String) {
        val index = shoppingList.indexOfFirst { it.id == itemId }
        if (index < 0) return
        val item = shoppingList[index]
        if (barcode !in item.barcodes) {
            shoppingList[index] = item.copy(barcodes = item.barcodes + barcode)
            saveToPrefs()
        }
    }

    override fun removeBarcode(itemId: String, barcode: String) {
        val index = shoppingList.indexOfFirst { it.id == itemId }
        if (index < 0) return
        val item = shoppingList[index]
        shoppingList[index] = item.copy(barcodes = item.barcodes - barcode)
        saveToPrefs()
    }

    override fun addBaselineItemToShoppingList(baselineItemId: String): Boolean {
        val baseline = BaselineData.baselineItems.find { it.id == baselineItemId } ?: return false
        val alreadyPresent = shoppingList.any { it.id == baselineItemId }
        if (alreadyPresent) return false
        shoppingList.add(baseline)
        saveToPrefs()
        return true
    }

    // ── Private helpers ──────────────────────────────────────────────────────

    private fun loadFromPrefs() {
        shoppingList.clear()
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
                shoppingList.add(
                    ShoppingItem(
                        id = obj.getString(FIELD_ID),
                        name = obj.getString(FIELD_NAME),
                        categoryId = obj.getString(FIELD_CATEGORY_ID),
                        isBaseline = obj.optBoolean(FIELD_IS_BASELINE, false),
                        barcodes = barcodes
                    )
                )
            }
        } catch (e: Exception) {
            shoppingList.clear()
        }
    }

    private fun saveToPrefs() {
        val array = JSONArray()
        for (item in shoppingList) {
            val obj = JSONObject()
            obj.put(FIELD_ID, item.id)
            obj.put(FIELD_NAME, item.name)
            obj.put(FIELD_CATEGORY_ID, item.categoryId)
            obj.put(FIELD_IS_BASELINE, item.isBaseline)
            val barcodes = JSONArray()
            item.barcodes.forEach { barcodes.put(it) }
            obj.put(FIELD_BARCODES, barcodes)
            array.put(obj)
        }
        prefs.edit().putString(KEY_SHOPPING_LIST, array.toString()).apply()
    }

    companion object {
        private const val PREFS_NAME = "shopping_prefs"
        private const val KEY_SHOPPING_LIST = "shopping_list_v1"
        private const val FIELD_ID = "id"
        private const val FIELD_NAME = "name"
        private const val FIELD_CATEGORY_ID = "category_id"
        private const val FIELD_IS_BASELINE = "is_baseline"
        private const val FIELD_BARCODES = "barcodes"

        private val itemComparator: Comparator<ShoppingItem> = compareBy(
            { BaselineData.categoryById(it.categoryId)?.name?.lowercase() ?: "" },
            { it.name.lowercase() }
        )
    }
}
