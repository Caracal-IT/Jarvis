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
        _shoppingList.value = repository.getShoppingList()
        _replenishList.value = repository.getReplenishList()
        _categories.value = repository.getCategories()
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
        _shoppingList.value = repository.getShoppingList()
        return true
    }

    /**
     * Renames an existing Shopping List item after validating the new name.
     *
     * @param itemId The ID of the item to rename.
     * @param newName The new display name.
     * @return True if the rename succeeded, false on validation or duplicate error.
     */
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
        _shoppingList.value = repository.getShoppingList()
        return true
    }

    /**
     * Removes an item from the Shopping List.
     *
     * @param itemId The ID of the item to remove.
     */
    fun removeShoppingItem(itemId: String) {
        repository.removeShoppingItem(itemId)
        _shoppingList.value = repository.getShoppingList()
    }

    /**
     * Associates a barcode with a Shopping List item.
     *
     * @param itemId The ID of the target item.
     * @param barcode The barcode string to add.
     */
    fun addBarcode(itemId: String, barcode: String) {
        repository.addBarcode(itemId, barcode)
        _shoppingList.value = repository.getShoppingList()
    }

    /**
     * Removes a barcode from a Shopping List item.
     *
     * @param itemId The ID of the target item.
     * @param barcode The barcode string to remove.
     */
    fun removeBarcode(itemId: String, barcode: String) {
        repository.removeBarcode(itemId, barcode)
        _shoppingList.value = repository.getShoppingList()
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
            _shoppingList.value = repository.getShoppingList()
        }
        return added
    }

    /** Clears the add-item error state after the dialog is dismissed. */
    fun clearAddItemError() {
        _addItemError.value = null
    }

    /** Clears the rename-item error state after the dialog is dismissed. */
    fun clearRenameItemError() {
        _renameItemError.value = null
    }
}
