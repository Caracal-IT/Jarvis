package com.github.caracal.jarvis.shopping.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.github.caracal.jarvis.R
import com.github.caracal.jarvis.databinding.ListItemCategoryHeaderBinding
import com.github.caracal.jarvis.databinding.ListItemShoppingBinding
import com.github.caracal.jarvis.shopping.ui.ShoppingDisplayItem

/**
 * Adapter for the Shopping List RecyclerView.
 *
 * Renders both category header rows and shopping item rows. Item rows expose a popup
 * menu with rename, remove, and barcode management options.
 *
 * @param onMenuRename Callback invoked when the user selects "Rename" for an item.
 * @param onMenuRemove Callback invoked when the user selects "Remove" for an item.
 * @param onMenuBarcodes Callback invoked when the user selects "Manage Barcodes" for an item.
 */
class ShoppingListAdapter(
    private val onMenuRename: (ShoppingDisplayItem.Item) -> Unit,
    private val onMenuRemove: (ShoppingDisplayItem.Item) -> Unit,
    private val onMenuBarcodes: (ShoppingDisplayItem.Item) -> Unit
) : ListAdapter<ShoppingDisplayItem, RecyclerView.ViewHolder>(DiffCallback()) {

    override fun getItemViewType(position: Int): Int =
        when (getItem(position)) {
            is ShoppingDisplayItem.Header -> VIEW_TYPE_HEADER
            is ShoppingDisplayItem.Item -> VIEW_TYPE_ITEM
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_HEADER -> HeaderViewHolder(
                ListItemCategoryHeaderBinding.inflate(inflater, parent, false)
            )
            else -> ItemViewHolder(
                ListItemShoppingBinding.inflate(inflater, parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val row = getItem(position)) {
            is ShoppingDisplayItem.Header -> (holder as HeaderViewHolder).bind(row)
            is ShoppingDisplayItem.Item -> (holder as ItemViewHolder).bind(
                row, onMenuRename, onMenuRemove, onMenuBarcodes
            )
        }
    }

    /** ViewHolder for category header rows. */
    class HeaderViewHolder(
        private val binding: ListItemCategoryHeaderBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        /** Binds the category name to the header text view. */
        fun bind(header: ShoppingDisplayItem.Header) {
            binding.tvCategoryName.text = header.category.name
        }
    }

    /** ViewHolder for shopping item rows. */
    class ItemViewHolder(
        private val binding: ListItemShoppingBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        /**
         * Binds the item data and wires the popup menu callbacks.
         *
         * @param row The display item containing the [com.github.caracal.jarvis.shopping.data.ShoppingItem].
         * @param onMenuRename Callback for rename selection.
         * @param onMenuRemove Callback for remove selection.
         * @param onMenuBarcodes Callback for barcode management selection.
         */
        fun bind(
            row: ShoppingDisplayItem.Item,
            onMenuRename: (ShoppingDisplayItem.Item) -> Unit,
            onMenuRemove: (ShoppingDisplayItem.Item) -> Unit,
            onMenuBarcodes: (ShoppingDisplayItem.Item) -> Unit
        ) {
            val item = row.item
            binding.tvItemName.text = item.name
            val barcodeCount = item.barcodes.size
            if (barcodeCount > 0) {
                binding.tvBarcodeCount.visibility = View.VISIBLE
                binding.tvBarcodeCount.text = binding.root.context.resources.getQuantityString(
                    R.plurals.barcode_count,
                    barcodeCount,
                    barcodeCount
                )
            } else {
                binding.tvBarcodeCount.visibility = View.GONE
            }
            binding.btnItemMenu.setOnClickListener { view ->
                val popup = PopupMenu(view.context, view)
                popup.inflate(R.menu.menu_shopping_item)
                popup.setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.action_rename -> { onMenuRename(row); true }
                        R.id.action_remove -> { onMenuRemove(row); true }
                        R.id.action_barcodes -> { onMenuBarcodes(row); true }
                        else -> false
                    }
                }
                popup.show()
            }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<ShoppingDisplayItem>() {
        override fun areItemsTheSame(
            oldItem: ShoppingDisplayItem,
            newItem: ShoppingDisplayItem
        ): Boolean =
            when {
                oldItem is ShoppingDisplayItem.Header && newItem is ShoppingDisplayItem.Header ->
                    oldItem.category.id == newItem.category.id
                oldItem is ShoppingDisplayItem.Item && newItem is ShoppingDisplayItem.Item ->
                    oldItem.item.id == newItem.item.id
                else -> false
            }

        override fun areContentsTheSame(
            oldItem: ShoppingDisplayItem,
            newItem: ShoppingDisplayItem
        ): Boolean = oldItem == newItem
    }

    companion object {
        private const val VIEW_TYPE_HEADER = 0
        private const val VIEW_TYPE_ITEM = 1
    }
}
