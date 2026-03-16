package com.github.caracal.jarvis.shopping.data

/**
 * Represents a single shopping item.
 *
 * @property id Unique GUID identifier for this item.
 * @property name Canonical generic name of the item.
 * @property categoryId The ID of the [ShoppingCategory] this item belongs to.
 * @property isBaseline True if this item is a predefined baseline item.
 * @property barcodes A list of barcodes associated with this item.
 * @property isOnShoppingList True if the item is currently active in the user's shopping list.
 */
data class ShoppingItem(
    val id: String,
    val name: String,
    val categoryId: String,
    val isBaseline: Boolean = false,
    val barcodes: List<String> = emptyList(),
    val isOnShoppingList: Boolean = true
)
