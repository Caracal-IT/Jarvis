package com.github.caracal.jarvis.groceries

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

/**
 * Represents a grocery item. Supports both static string resources (predefined items)
 * and dynamic names (items discovered via barcode lookup or manual entry).
 */
sealed class GroceryItem {
    abstract val iconRes: Int
    abstract val iconBgRes: Int
    /** All barcodes linked to this item. An item can have multiple barcodes (e.g. different sizes). */
    abstract val barcodes: MutableSet<String>

    /** Returns the display name using the provided resolver. */
    abstract fun getName(resolver: (Int) -> String): String

    /** Returns true if this item is linked to the given barcode. */
    fun hasBarcode(barcode: String) = barcodes.contains(barcode)

    /** Predefined item backed by a string resource. */
    data class Static(
        @param:StringRes val nameRes: Int,
        @param:DrawableRes override val iconRes: Int,
        @param:DrawableRes override val iconBgRes: Int,
        override val barcodes: MutableSet<String> = mutableSetOf()
    ) : GroceryItem() {
        override fun getName(resolver: (Int) -> String) = resolver(nameRes)
    }

    /** Dynamically created item with a runtime name (from barcode lookup or manual entry). */
    data class Dynamic(
        val name: String,
        @param:DrawableRes override val iconRes: Int,
        @param:DrawableRes override val iconBgRes: Int,
        override val barcodes: MutableSet<String> = mutableSetOf()
    ) : GroceryItem() {
        override fun getName(resolver: (Int) -> String) = name
    }
}
