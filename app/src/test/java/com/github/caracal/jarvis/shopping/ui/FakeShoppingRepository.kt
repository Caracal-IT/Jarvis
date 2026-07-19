package com.github.caracal.jarvis.shopping.ui

import com.github.caracal.jarvis.shopping.data.ShoppingCategory
import com.github.caracal.jarvis.shopping.data.ShoppingItem
import com.github.caracal.jarvis.shopping.data.ShoppingRepository
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

/**
 * In-memory [ShoppingRepository] test double for exercising [ShoppingViewModel] without
 * a real SharedPreferences-backed data source.
 */
class FakeShoppingRepository(
    initialItems: List<ShoppingItem> = emptyList(),
    private val categories: List<ShoppingCategory> = listOf(ShoppingCategory("cat_1", "Category 1"))
) : ShoppingRepository {

    private val items: MutableList<ShoppingItem> = initialItems.toMutableList()
    private val changeListeners: MutableList<() -> Unit> = mutableListOf()
    private var lastModified: Long = 0L

    override fun getShoppingList(): List<ShoppingItem> = items.filter { it.isOnShoppingList }

    override fun getReplenishList(): List<ShoppingItem> = items.filter { !it.isOnShoppingList || it.isBaseline }

    override fun getCategories(): List<ShoppingCategory> = categories

    override fun updateShoppingItem(
        itemId: String,
        newName: String,
        newCategoryId: String,
        newBarcodes: List<String>
    ): Boolean {
        val index = items.indexOfFirst { it.id == itemId }
        if (index < 0) return false
        val duplicate = items.any {
            it.id != itemId && it.name.equals(newName, ignoreCase = true) && it.categoryId == newCategoryId
        }
        if (duplicate) return false
        items[index] = items[index].copy(name = newName, categoryId = newCategoryId, barcodes = newBarcodes)
        return true
    }

    override fun findByBarcode(barcode: String): ShoppingItem? = items.find { barcode in it.barcodes }

    override fun addShoppingItemWithBarcode(
        name: String,
        categoryId: String,
        barcode: String,
        isBaseline: Boolean
    ): Boolean {
        val duplicate = items.any { it.name.equals(name, ignoreCase = true) && it.categoryId == categoryId }
        if (duplicate) return false
        items.add(
            ShoppingItem(
                id = UUID.randomUUID().toString(),
                name = name,
                categoryId = categoryId,
                isBaseline = isBaseline,
                barcodes = listOf(barcode),
                isOnShoppingList = true
            )
        )
        return true
    }

    override fun addShoppingItem(name: String, categoryId: String, isBaseline: Boolean): ShoppingItem {
        val existing = items.find { it.name.equals(name, ignoreCase = true) && it.categoryId == categoryId }
        if (existing != null) {
            val updated = existing.copy(isOnShoppingList = true, isBaseline = existing.isBaseline || isBaseline)
            items[items.indexOf(existing)] = updated
            return updated
        }
        val newItem = ShoppingItem(
            id = UUID.randomUUID().toString(),
            name = name,
            categoryId = categoryId,
            isBaseline = isBaseline,
            isOnShoppingList = true
        )
        items.add(newItem)
        return newItem
    }

    override fun renameShoppingItem(itemId: String, newName: String): Boolean {
        val index = items.indexOfFirst { it.id == itemId }
        if (index < 0) return false
        val item = items[index]
        val duplicate = items.any {
            it.id != itemId && it.name.equals(newName, ignoreCase = true) && it.categoryId == item.categoryId
        }
        if (duplicate) return false
        items[index] = item.copy(name = newName)
        return true
    }

    override fun removeShoppingItem(itemId: String) {
        val index = items.indexOfFirst { it.id == itemId }
        if (index < 0) return
        val item = items[index]
        if (item.isBaseline) {
            items[index] = item.copy(isOnShoppingList = false)
        } else {
            items.removeAt(index)
        }
    }

    override fun addBarcode(itemId: String, barcode: String) {
        val index = items.indexOfFirst { it.id == itemId }
        if (index < 0) return
        val item = items[index]
        items[index] = item.copy(barcodes = (item.barcodes + barcode).distinct())
    }

    override fun removeBarcode(itemId: String, barcode: String) {
        val index = items.indexOfFirst { it.id == itemId }
        if (index < 0) return
        val item = items[index]
        items[index] = item.copy(barcodes = item.barcodes - barcode)
    }

    override fun addBaselineItemToShoppingList(baselineItemId: String): Boolean {
        val index = items.indexOfFirst { it.id == baselineItemId }
        if (index < 0) return false
        val item = items[index]
        if (item.isOnShoppingList) return false
        items[index] = item.copy(isOnShoppingList = true)
        return true
    }

    override fun addChangeListener(listener: () -> Unit) {
        changeListeners.add(listener)
    }

    override fun exportSnapshot(): String {
        val array = JSONArray()
        items.forEach { item ->
            val obj = JSONObject()
            obj.put("id", item.id)
            obj.put("name", item.name)
            obj.put("category_id", item.categoryId)
            obj.put("is_baseline", item.isBaseline)
            obj.put("is_on_shopping_list", item.isOnShoppingList)
            obj.put("barcodes", JSONArray(item.barcodes))
            array.put(obj)
        }
        return JSONObject().apply {
            put("timestamp", lastModified)
            put("items", array)
        }.toString()
    }

    override fun applyRemoteSnapshot(json: String): Boolean {
        val state = JSONObject(json)
        val remoteTimestamp = state.optLong("timestamp", -1L)
        if (remoteTimestamp <= lastModified) return false

        val array = state.optJSONArray("items") ?: JSONArray()
        items.clear()
        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            val barcodes = mutableListOf<String>()
            val barcodesArray = obj.optJSONArray("barcodes")
            if (barcodesArray != null) {
                for (j in 0 until barcodesArray.length()) barcodes.add(barcodesArray.getString(j))
            }
            items.add(
                ShoppingItem(
                    id = obj.getString("id"),
                    name = obj.getString("name"),
                    categoryId = obj.getString("category_id"),
                    isBaseline = obj.optBoolean("is_baseline", false),
                    barcodes = barcodes,
                    isOnShoppingList = obj.optBoolean("is_on_shopping_list", true)
                )
            )
        }
        lastModified = remoteTimestamp
        changeListeners.toList().forEach { it() }
        return true
    }
}
