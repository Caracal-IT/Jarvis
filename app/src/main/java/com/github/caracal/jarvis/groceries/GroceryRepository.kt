package com.github.caracal.jarvis.groceries

import com.github.caracal.jarvis.R

/**
 * Singleton repository holding the shared grocery list and inventory list.
 * Ensures both lists stay unique and in sync across fragments.
 */
object GroceryRepository {

    /** Master list of all known items — never shrinks unless explicitly deleted from inventory. */
    private val allItems: List<GroceryItem> = listOf(
        GroceryItem(R.string.item_apple,   R.drawable.ic_apple,   R.drawable.bg_icon_red),
        GroceryItem(R.string.item_bread,   R.drawable.ic_bread,   R.drawable.bg_icon_orange),
        GroceryItem(R.string.item_milk,    R.drawable.ic_milk,    R.drawable.bg_icon_blue),
        GroceryItem(R.string.item_eggs,    R.drawable.ic_egg,     R.drawable.bg_icon_yellow),
        GroceryItem(R.string.item_cheese,  R.drawable.ic_cheese,  R.drawable.bg_icon_navy),
        GroceryItem(R.string.item_water,   R.drawable.ic_water,   R.drawable.bg_icon_blue),
        GroceryItem(R.string.item_butter,  R.drawable.ic_milk,    R.drawable.bg_icon_orange),
        GroceryItem(R.string.item_yogurt,  R.drawable.ic_milk,    R.drawable.bg_icon_white),
        GroceryItem(R.string.item_peanuts, R.drawable.ic_peanuts, R.drawable.bg_icon_dark_green)
    )

    /** Active grocery list — starts empty. Items are added from inventory via right swipe. */
    val groceryList: MutableList<GroceryItem> = mutableListOf()

    /**
     * Persistent inventory memory — pre-populated with all known items.
     * Never loses an item unless explicitly deleted from inventory.
     */
    private val inventoryMemory: MutableList<GroceryItem> = allItems.toMutableList()

    /**
     * Returns the inventory filtered to exclude items currently in the grocery list.
     * Called fresh each time the inventory is displayed.
     */
    fun visibleInventory(): List<GroceryItem> =
        inventoryMemory.filter { inv -> groceryList.none { g -> g.nameRes == inv.nameRes } }

    /**
     * The live list backing the inventory adapter.
     * Refreshed from visibleInventory() before display.
     */
    val inventoryList: MutableList<GroceryItem> = mutableListOf()

    /** Syncs inventoryList with what should be visible (not in grocery list). */
    fun refreshInventoryList() {
        inventoryList.clear()
        inventoryList.addAll(visibleInventory())
    }

    /** Moves item from grocery list to inventory memory (if not already remembered). */
    fun moveToInventory(item: GroceryItem, position: Int) {
        groceryList.removeAt(position)
        if (inventoryMemory.none { it.nameRes == item.nameRes }) {
            inventoryMemory.add(item)
        }
    }

    /** Moves item from inventory to grocery list. Remembered in inventory but hidden while in list. */
    fun moveToGroceries(item: GroceryItem, position: Int) {
        inventoryList.removeAt(position)
        if (groceryList.none { it.nameRes == item.nameRes }) {
            groceryList.add(item)
        }
    }

    /**
     * Removes item from grocery list. Item is still remembered in inventory and
     * will reappear in inventory display since it's no longer in the grocery list.
     */
    fun removeFromGroceries(item: GroceryItem, position: Int) {
        groceryList.removeAt(position)
        if (inventoryMemory.none { it.nameRes == item.nameRes }) {
            inventoryMemory.add(item)
        }
    }

    /** Permanently forgets item from inventory memory and display. */
    fun removeFromInventory(position: Int) {
        val item = inventoryList[position]
        inventoryList.removeAt(position)
        inventoryMemory.removeAll { it.nameRes == item.nameRes }
    }

    /** Links a barcode string to an item in inventory memory. */
    fun linkBarcode(barcode: String, item: GroceryItem) {
        inventoryMemory.find { it.nameRes == item.nameRes }?.barcode = barcode
        groceryList.find { it.nameRes == item.nameRes }?.barcode = barcode
    }

    /** Returns the inventory item linked to the given barcode, or null if not found. */
    fun findByBarcode(barcode: String): GroceryItem? =
        inventoryMemory.find { it.barcode == barcode }

    /**
     * Adds item to grocery list by barcode if linked and not already in the list.
     * Returns true if added, false if already in list or not linked.
     */
    fun addToGroceriesByBarcode(barcode: String): Boolean {
        val item = findByBarcode(barcode) ?: return false
        if (groceryList.any { it.nameRes == item.nameRes }) return false
        groceryList.add(item)
        return true
    }
}
