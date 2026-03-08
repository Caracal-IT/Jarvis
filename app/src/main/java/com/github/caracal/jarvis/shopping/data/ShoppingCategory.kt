package com.github.caracal.jarvis.shopping.data

/**
 * Represents a category grouping for shopping items.
 *
 * @property id Unique GUID identifier for this category.
 * @property name Human-readable category name.
 */
data class ShoppingCategory(
    val id: String,
    val name: String
)
