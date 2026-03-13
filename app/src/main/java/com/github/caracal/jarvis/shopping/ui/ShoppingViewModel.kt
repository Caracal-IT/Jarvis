package com.github.caracal.jarvis.shopping.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.caracal.jarvis.shopping.data.NamingValidator
import com.github.caracal.jarvis.shopping.data.ShoppingCategory
import com.github.caracal.jarvis.shopping.data.ShoppingItem
import com.github.caracal.jarvis.shopping.data.ShoppingRepository

/**
 * ViewModel for the Shopping feature.
 *
 * Manages the Shopping List and Replenish List state, delegating all persistence
 * to [ShoppingRepository]. Exposes LiveData streams consumed by child fragments.
 *
 * @param repository The data source for all shopping operations.
 */
class ShoppingViewModel(private val repository: ShoppingRepository) : ViewModel() {

    private val _shoppingList = MutableLiveData<List<ShoppingItem>>()
    /** The current Shopping List items. */
    val shoppingList: LiveData<List<ShoppingItem>> = _shoppingList

    private val _replenishList = MutableLiveData<List<ShoppingItem>>()
    /** The baseline items available for replenishment. */
    val replenishList: LiveData<List<ShoppingItem>> = _replenishList

    private val _categories = MutableLiveData<List<ShoppingCategory>>()
    /** All available shopping categories. */
    val categories: LiveData<List<ShoppingCategory>> = _categories

    private val _addItemError = MutableLiveData<String?>()
    /** Validation or duplication error for the Add Item dialog, or null when cleared. */
    val addItemError: LiveData<String?> = _addItemError

    private val _renameItemError = MutableLiveData<String?>()
    /** Validation or duplication error for the Rename Item dialog, or null when cleared. */
    val renameItemError: LiveData<String?> = _renameItemError

    init {
        refresh()
    }

    /** Reloads all lists and categories from the repository. */
    fun refresh() {
        refreshLists()
        _categories.value = repository.getCategories()
    }

    private fun refreshLists() {
        _shoppingList.value = repository.getShoppingList()
        _replenishList.value = repository.getReplenishList()
    }

    /**
     * Adds a new item to the Shopping List with an initial barcode attached.
     *
     * @param name The display name for the new item.
     * @param categoryId The ID of the category to assign.
     * @param barcode The barcode to attach immediately.
     * @return True if the item was added successfully.
     */
    fun addShoppingItemWithBarcode(name: String, categoryId: String, barcode: String): Boolean {
        val validation = NamingValidator.validate(name)
        if (!validation.isValid) {
            _addItemError.value = validation.errorMessage
            return false
        }
        val added = repository.addShoppingItemWithBarcode(name, categoryId, barcode)
        if (!added) {
            _addItemError.value = "An item with that name already exists in this category."
            return false
        }
        _addItemError.value = null
        refreshLists()
        return true
    }

    /**
     * Adds a new item to the Shopping List after validating the name.
     *
     * @param name The display name for the new item.
     * @param categoryId The ID of the category to assign.
     * @return True if the item was added successfully, false on validation or duplicate error.
     */
    fun addShoppingItem(name: String, categoryId: String): Boolean {
        val validation = NamingValidator.validate(name)
        if (!validation.isValid) {
            _addItemError.value = validation.errorMessage
            return false
        }
        val result = repository.addShoppingItem(name, categoryId)
        if (result == null) {
            _addItemError.value = "An item with that name already exists in this category."
            return false
        }
        _addItemError.value = null
        refreshLists()
        return true
    }

    /**
     * Updates an existing Shopping List item's name, category and barcodes.
     *
     * @param itemId The ID of the item to update.
     * @param newName The new display name.
     * @param newCategoryId The new category ID.
     * @param newBarcodes The full replacement barcode list.
     * @return True if the update succeeded, false on validation or duplicate error.
     */
    fun updateShoppingItem(
        itemId: String,
        newName: String,
        newCategoryId: String,
        newBarcodes: List<String>
    ): Boolean {
        val validation = NamingValidator.validate(newName)
        if (!validation.isValid) {
            _renameItemError.value = validation.errorMessage
            return false
        }
        val success = repository.updateShoppingItem(itemId, newName, newCategoryId, newBarcodes)
        if (!success) {
            _renameItemError.value = "An item with that name already exists in this category."
            return false
        }
        _renameItemError.value = null
        refreshLists()
        return true
    }

    /**
     * Finds a Shopping List item by barcode.
     *
     * @param barcode The barcode string to search for.
     * @return The matching [ShoppingItem] or null.
     */
    fun findByBarcode(barcode: String) = repository.findByBarcode(barcode)

    /**
     * Renames an existing Shopping List item after validating the new name.
     *
     * @param itemId The ID of the item to rename.
     * @param newName The new display name.
     * @return True if the rename succeeded, false on validation or duplicate error.
     */
    @Suppress("unused")
    fun renameShoppingItem(itemId: String, newName: String): Boolean {
        val validation = NamingValidator.validate(newName)
        if (!validation.isValid) {
            _renameItemError.value = validation.errorMessage
            return false
        }
        val success = repository.renameShoppingItem(itemId, newName)
        if (!success) {
            _renameItemError.value = "An item with that name already exists in this category."
            return false
        }
        _renameItemError.value = null
        refreshLists()
        return true
    }

    /**
     * Removes an item from the Shopping List.
     *
     * @param itemId The ID of the item to remove.
     */
    fun removeShoppingItem(itemId: String) {
        repository.removeShoppingItem(itemId)
        refreshLists()
    }


    /**
     * Adds a baseline item to the Shopping List.
     *
     * @param baselineItemId The ID of the baseline item.
     * @return True if added, false if already present.
     */
    fun addBaselineItemToShoppingList(baselineItemId: String): Boolean {
        val added = repository.addBaselineItemToShoppingList(baselineItemId)
        if (added) {
            refreshLists()
        }
        return added
    }

    /** Clears the add-item error state after the dialog is dismissed. */
    fun clearAddItemError() {
        _addItemError.value = null
    }

    /** Clears the rename/edit-item error state after the dialog is dismissed. */
    fun clearRenameItemError() {
        _renameItemError.value = null
    }
}
