package com.github.caracal.jarvis.shopping.ui

import com.github.caracal.jarvis.shopping.data.ShoppingCategory
import com.github.caracal.jarvis.shopping.data.ShoppingItem

/**
 * Sealed class representing a display row in a shopping-related RecyclerView.
 *
 * Used by both [com.github.caracal.jarvis.shopping.list.ShoppingListAdapter] and
 * [com.github.caracal.jarvis.shopping.replenish.ReplenishListAdapter] to support
 * mixed-type lists with category headers.
 */
sealed class ShoppingDisplayItem {
    /** A category section header row. */
    data class Header(val category: ShoppingCategory) : ShoppingDisplayItem()

    /** A shopping item row. */
    data class Item(val item: ShoppingItem) : ShoppingDisplayItem()
}
