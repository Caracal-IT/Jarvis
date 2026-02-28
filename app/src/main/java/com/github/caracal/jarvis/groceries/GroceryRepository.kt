package com.github.caracal.jarvis.groceries

import com.github.caracal.jarvis.R

/**
 * Singleton repository holding the shared grocery list and base items list.
 * Ensures both lists stay unique and in sync across fragments.
 */
object GroceryRepository {

    /** Master list of all known items — never shrinks unless explicitly deleted from base items. */
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

    /** Active grocery list — starts empty. Items are added from base items via right swipe. */
    val groceryList: MutableList<GroceryItem> = mutableListOf()

    /**
     * Persistent base items memory — pre-populated with all known items.
     * Never loses an item unless explicitly deleted from base items.
     */
    private val baseItemsMemory: MutableList<GroceryItem> = allItems.toMutableList()

    /**
     * Returns the base items filtered to exclude items currently in the grocery list.
     * Called fresh each time the base items screen is displayed.
     */
    fun visibleBaseItems(): List<GroceryItem> =
        baseItemsMemory.filter { base -> groceryList.none { g -> isSameItem(g, base) } }

    /**
     * The live list backing the base items adapter.
     * Refreshed from visibleBaseItems() before display.
     */
    val baseItemsList: MutableList<GroceryItem> = mutableListOf()

    /** Syncs baseItemsList with what should be visible (not in grocery list). */
    fun refreshBaseItemsList() {
        baseItemsList.clear()
        baseItemsList.addAll(visibleBaseItems())
    }

    /** Returns true if two items represent the same product. */
    private fun isSameItem(a: GroceryItem, b: GroceryItem): Boolean {
        if (a.barcodes.isNotEmpty() && a.barcodes.any { b.barcodes.contains(it) }) return true
        if (a is GroceryItem.Static && b is GroceryItem.Static) return a.nameRes == b.nameRes
        if (a is GroceryItem.Dynamic && b is GroceryItem.Dynamic) return a.name.equals(b.name, ignoreCase = true)
        return false
    }

    /** Moves item from grocery list back to base items memory (if not already remembered). */
    fun moveToBaseItems(item: GroceryItem, position: Int) {
        groceryList.removeAt(position)
        if (baseItemsMemory.none { isSameItem(it, item) }) {
            baseItemsMemory.add(item)
        }
    }

    /** Moves item from base items to grocery list. Remembered in base items but hidden while in list. */
    fun moveToGroceries(item: GroceryItem, position: Int) {
        baseItemsList.removeAt(position)
        if (groceryList.none { isSameItem(it, item) }) {
            groceryList.add(item)
        }
    }

    /** Removes item from grocery list. Item is still remembered in base items. */
    fun removeFromGroceries(item: GroceryItem, position: Int) {
        groceryList.removeAt(position)
        if (baseItemsMemory.none { isSameItem(it, item) }) {
            baseItemsMemory.add(item)
        }
    }

    /** Permanently forgets item from base items memory and display. */
    fun removeFromBaseItems(position: Int) {
        val item = baseItemsList[position]
        baseItemsList.removeAt(position)
        baseItemsMemory.removeAll { isSameItem(it, item) }
    }

    /** Checks if base items already has an item linked to this barcode. */
    fun findByBarcode(barcode: String): GroceryItem? =
        baseItemsMemory.find { it.hasBarcode(barcode) }

    /** Checks if base items already has an item with this name (case-insensitive). */
    fun findByName(name: String, resolver: (Int) -> String): GroceryItem? =
        baseItemsMemory.find { it.getName(resolver).equals(name, ignoreCase = true) }

    /**
     * Adds a dynamic item to base items and optionally the grocery list.
     * If an item with the same name already exists, the barcode is added to it instead
     * of creating a duplicate.
     */
    fun addDynamicItem(item: GroceryItem.Dynamic, addToGroceries: Boolean) {
        val barcode = item.barcodes.firstOrNull()

        // Find existing item by name (case-insensitive)
        val existingByName = baseItemsMemory.find { existing ->
            existing is GroceryItem.Dynamic && existing.name.equals(item.name, ignoreCase = true)
        }

        val itemToAdd = when {
            existingByName != null -> {
                // Item exists — just add the new barcode to it
                if (barcode != null) existingByName.barcodes.add(barcode)
                existingByName
            }
            barcode != null && baseItemsMemory.any { it.hasBarcode(barcode) } -> {
                // Barcode already linked to another item — use that item
                baseItemsMemory.first { it.hasBarcode(barcode) }
            }
            else -> {
                // Genuinely new item — add to base items
                baseItemsMemory.add(item)
                item
            }
        }

        if (addToGroceries && groceryList.none { isSameItem(it, itemToAdd) }) {
            groceryList.add(itemToAdd)
        }
    }

    /**
     * Renames an item everywhere it appears (baseItemsMemory, baseItemsList, groceryList).
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
                    name      = newName,
                    iconRes   = item.iconRes,
                    iconBgRes = item.iconBgRes,
                    barcodes  = item.barcodes
                )
            }
            this[idx] = renamed
            return renamed
        }

        // Replace in all three lists — whichever holds the item
        val renamed = baseItemsMemory.replaceItem()
            ?: baseItemsList.replaceItem()
            ?: groceryList.replaceItem()
            ?: item

        // Ensure the same renamed instance is propagated to the other two lists
        listOf(baseItemsMemory, baseItemsList, groceryList).forEach { list ->
            val idx = list.indexOfFirst { it === item }
            if (idx != -1) list[idx] = renamed
        }

        return renamed
    }

    /**
     * Removes an item from every list by reference.
     * Used after a merge to eliminate the duplicate.
     */
    fun removeDuplicate(item: GroceryItem) {
        baseItemsMemory.removeAll { it === item }
        baseItemsList.removeAll { it === item }
        groceryList.removeAll { it === item }
    }

    /**
     * Links a barcode to an existing item across all lists.
     */
    fun linkBarcode(barcode: String, item: GroceryItem) {
        listOf(baseItemsMemory, baseItemsList, groceryList).forEach { list ->
            list.find { it === item }?.barcodes?.add(barcode)
        }
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
