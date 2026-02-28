package com.github.caracal.jarvis.groceries

import com.github.caracal.jarvis.R

/**
 * Singleton repository holding the shared grocery list and inventory list.
 * Ensures both lists stay unique and in sync across fragments.
 */
object GroceryRepository {

    /** Master list of all known items — never shrinks unless explicitly deleted from inventory. */
    private val allItems: List<GroceryItem> = listOf(
        GroceryItem.Static(R.string.item_apple,   R.drawable.ic_apple,   R.drawable.bg_icon_red),
        GroceryItem.Static(R.string.item_bread,   R.drawable.ic_bread,   R.drawable.bg_icon_orange),
        GroceryItem.Static(R.string.item_milk,    R.drawable.ic_milk,    R.drawable.bg_icon_blue),
        GroceryItem.Static(R.string.item_eggs,    R.drawable.ic_egg,     R.drawable.bg_icon_yellow),
        GroceryItem.Static(R.string.item_cheese,  R.drawable.ic_cheese,  R.drawable.bg_icon_navy),
        GroceryItem.Static(R.string.item_water,   R.drawable.ic_water,   R.drawable.bg_icon_blue),
        GroceryItem.Static(R.string.item_butter,  R.drawable.ic_milk,    R.drawable.bg_icon_orange),
        GroceryItem.Static(R.string.item_yogurt,  R.drawable.ic_milk,    R.drawable.bg_icon_white),
        GroceryItem.Static(R.string.item_peanuts, R.drawable.ic_peanuts, R.drawable.bg_icon_dark_green)
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
        inventoryMemory.filter { inv -> groceryList.none { g -> isSameItem(g, inv) } }

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

    /** Returns true if two items represent the same product. */
    private fun isSameItem(a: GroceryItem, b: GroceryItem): Boolean {
        if (a.barcodes.isNotEmpty() && a.barcodes.any { b.barcodes.contains(it) }) return true
        if (a is GroceryItem.Static && b is GroceryItem.Static) return a.nameRes == b.nameRes
        if (a is GroceryItem.Dynamic && b is GroceryItem.Dynamic) return a.name.equals(b.name, ignoreCase = true)
        return false
    }

    /** Moves item from grocery list to inventory memory (if not already remembered). */
    fun moveToInventory(item: GroceryItem, position: Int) {
        groceryList.removeAt(position)
        if (inventoryMemory.none { isSameItem(it, item) }) {
            inventoryMemory.add(item)
        }
    }

    /** Moves item from inventory to grocery list. Remembered in inventory but hidden while in list. */
    fun moveToGroceries(item: GroceryItem, position: Int) {
        inventoryList.removeAt(position)
        if (groceryList.none { isSameItem(it, item) }) {
            groceryList.add(item)
        }
    }

    /** Removes item from grocery list. Item is still remembered in inventory. */
    fun removeFromGroceries(item: GroceryItem, position: Int) {
        groceryList.removeAt(position)
        if (inventoryMemory.none { isSameItem(it, item) }) {
            inventoryMemory.add(item)
        }
    }

    /** Permanently forgets item from inventory memory and display. */
    fun removeFromInventory(position: Int) {
        val item = inventoryList[position]
        inventoryList.removeAt(position)
        inventoryMemory.removeAll { isSameItem(it, item) }
    }

    /** Checks if inventory already has an item linked to this barcode. */
    fun findByBarcode(barcode: String): GroceryItem? =
        inventoryMemory.find { it.hasBarcode(barcode) }

    /** Checks if inventory already has an item with this name (case-insensitive). */
    fun findByName(name: String, resolver: (Int) -> String): GroceryItem? =
        inventoryMemory.find { it.getName(resolver).equals(name, ignoreCase = true) }

    /**
     * Adds a dynamic item to inventory and optionally the grocery list.
     * If an item with the same name already exists, the barcode is added to it instead
     * of creating a duplicate.
     */
    fun addDynamicItem(item: GroceryItem.Dynamic, addToGroceries: Boolean) {
        val barcode = item.barcodes.firstOrNull()

        // Find existing item by name (case-insensitive)
        val existingByName = inventoryMemory.find { existing ->
            existing is GroceryItem.Dynamic && existing.name.equals(item.name, ignoreCase = true)
        }

        val itemToAdd = when {
            existingByName != null -> {
                // Item exists — just add the new barcode to it
                if (barcode != null) existingByName.barcodes.add(barcode)
                existingByName
            }
            barcode != null && inventoryMemory.any { it.hasBarcode(barcode) } -> {
                // Barcode already linked to another item — use that item
                inventoryMemory.first { it.hasBarcode(barcode) }
            }
            else -> {
                // Genuinely new item — add to inventory
                inventoryMemory.add(item)
                item
            }
        }

        if (addToGroceries && groceryList.none { isSameItem(it, itemToAdd) }) {
            groceryList.add(itemToAdd)
        }
    }

    /**
     * Renames an item everywhere it appears (inventoryMemory, inventoryList, groceryList).
     * Dynamic items are renamed in-place. Static items are replaced with a Dynamic copy.
     * Returns the renamed item so the caller can refresh its reference.
     */
    fun renameItem(item: GroceryItem, newName: String): GroceryItem {
        fun MutableList<GroceryItem>.replaceItem(): GroceryItem? {
            val idx = indexOfFirst { it === item }
            if (idx == -1) return null
            val renamed = when (item) {
                is GroceryItem.Dynamic -> item.copy(name = newName)
                is GroceryItem.Static  -> GroceryItem.Dynamic(
                    name     = newName,
                    iconRes  = item.iconRes,
                    iconBgRes = item.iconBgRes,
                    barcodes = item.barcodes
                )
            }
            this[idx] = renamed
            return renamed
        }

        // Replace in all three lists — whichever holds the item
        val renamed = inventoryMemory.replaceItem()
            ?: inventoryList.replaceItem()
            ?: groceryList.replaceItem()
            ?: item

        // Ensure the same renamed instance is propagated to the other two lists
        listOf(inventoryMemory, inventoryList, groceryList).forEach { list ->
            val idx = list.indexOfFirst { it === item }
            if (idx != -1) list[idx] = renamed
        }

        return renamed
    }

    /**
     * Removes an item from every list by reference.
     * Used after a merge where a renamed item's barcodes have been transferred
     * to an existing item, making this one a duplicate that must be discarded.
     */
    fun removeDuplicate(item: GroceryItem) {
        inventoryMemory.removeAll { it === item }
        inventoryList.removeAll { it === item }
        groceryList.removeAll { it === item }
    }

    /**
     * Links a barcode to an existing item across all lists.
     * Adds to the item's barcode set — does not replace existing barcodes.
     */
    fun linkBarcode(barcode: String, item: GroceryItem) {
        listOf(inventoryMemory, inventoryList, groceryList).forEach { list ->
            list.find { it === item }?.barcodes?.add(barcode)
        }
        // Also add the barcode directly to the passed item reference
        item.barcodes.add(barcode)
    }

    /**
     * Adds the item linked to the barcode to the grocery list.
     * Returns true if added, false if already in list or barcode not linked.
     */
    fun addToGroceriesByBarcode(barcode: String): Boolean {
        val item = findByBarcode(barcode) ?: return false
        if (groceryList.any { isSameItem(it, item) }) return false
        groceryList.add(item)
        return true
    }
}
