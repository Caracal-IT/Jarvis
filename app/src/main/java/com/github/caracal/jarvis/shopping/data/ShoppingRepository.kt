package com.github.caracal.jarvis.shopping.data

/**
 * Repository interface for managing shopping and replenish list data.
 *
 * Provides CRUD operations for [ShoppingItem] entities and read access to
 * [ShoppingCategory] definitions. Implementations must persist data across
 * app and device restarts.
 */
interface ShoppingRepository {

    /** Returns all items currently in the Shopping List, sorted by category then name. */
    fun getShoppingList(): List<ShoppingItem>

    /** Returns all baseline items for the Replenish List, sorted by category then name. */
    fun getReplenishList(): List<ShoppingItem>

    /** Returns all available categories. */
    fun getCategories(): List<ShoppingCategory>

    /**
     * Updates an existing Shopping List item's name, category and barcodes.
     *
     * @param itemId The ID of the item to update.
     * @param newName The new canonical name.
     * @param newCategoryId The new category ID.
     * @param newBarcodes The full replacement barcode list.
     * @return True if the update succeeded, false if a duplicate name exists or item not found.
     */
    fun updateShoppingItem(
        itemId: String,
        newName: String,
        newCategoryId: String,
        newBarcodes: List<String>
    ): Boolean

    /**
     * Finds a Shopping List item by barcode.
     *
     * @param barcode The barcode to search for.
     * @return The [ShoppingItem] that has this barcode, or null if not found.
     */
    fun findByBarcode(barcode: String): ShoppingItem?

    /**
     * Adds a new item to the Shopping List with an initial barcode.
     *
     * @param name Canonical generic name for the new item.
     * @param categoryId The ID of the category this item belongs to.
     * @param barcode The initial barcode to attach.
     * @return True if the item was added, false if a duplicate exists.
     */
    fun addShoppingItemWithBarcode(name: String, categoryId: String, barcode: String): Boolean

    /**
     * Adds a new item to the Shopping List.
     *
     * @param name Canonical generic name for the new item.
     * @param categoryId The ID of the category this item belongs to.
     * @return The newly created [ShoppingItem], or null if a duplicate exists.
     */
    fun addShoppingItem(name: String, categoryId: String): ShoppingItem?

    /**
     * Renames an existing Shopping List item.
     *
     * @param itemId The ID of the item to rename.
     * @param newName The new canonical generic name.
     * @return True if the rename succeeded, false if a duplicate name exists or item not found.
     */
    fun renameShoppingItem(itemId: String, newName: String): Boolean

    /**
     * Removes an item from the Shopping List.
     *
     * Baseline items removed from the Shopping List continue to appear in the Replenish List.
     *
     * @param itemId The ID of the item to remove.
     */
    fun removeShoppingItem(itemId: String)

    /**
     * Adds a barcode to a Shopping List item.
     *
     * @param itemId The ID of the target item.
     * @param barcode The barcode string to add.
     */
    fun addBarcode(itemId: String, barcode: String)

    /**
     * Removes a barcode from a Shopping List item.
     *
     * @param itemId The ID of the target item.
     * @param barcode The barcode string to remove.
     */
    fun removeBarcode(itemId: String, barcode: String)

    /**
     * Adds a baseline item to the Shopping List.
     *
     * @param baselineItemId The ID of the baseline item to add.
     * @return True if the item was added, false if it is already in the Shopping List.
     */
    fun addBaselineItemToShoppingList(baselineItemId: String): Boolean
}
