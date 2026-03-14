package com.github.caracal.jarvis.shopping.replenish

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.github.caracal.jarvis.R
import com.github.caracal.jarvis.databinding.ListItemCategoryHeaderBinding
import com.github.caracal.jarvis.databinding.ShoppingReplenishItemBinding
import com.github.caracal.jarvis.shopping.ui.ShoppingDisplayItem

/**
 * Adapter for the Replenish List RecyclerView.
 *
 * Renders items grouped by category header. Each item row has an "Add" button
 * and a barcode scan icon.
 *
 * @param onAddToList Callback invoked when the user taps "Add" for an item.
 * @param onItemBarcode Callback invoked when the barcode scan icon is tapped.
 */
class ReplenishListAdapter(
    private val onAddToList: (ShoppingDisplayItem.Item) -> Unit,
    private val onItemBarcode: (ShoppingDisplayItem.Item) -> Unit
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
                ShoppingReplenishItemBinding.inflate(inflater, parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val row = getItem(position)) {
            is ShoppingDisplayItem.Header -> (holder as HeaderViewHolder).bind(row)
            is ShoppingDisplayItem.Item -> (holder as ItemViewHolder).bind(row, onAddToList, onItemBarcode)
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

    /** ViewHolder for replenish item rows. */
    class ItemViewHolder(
        private val binding: ShoppingReplenishItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        /**
         * Binds the item data and wires action buttons.
         *
         * @param row The display item containing the [com.github.caracal.jarvis.shopping.data.ShoppingItem].
         * @param onAddToList Callback for the "Add" button.
         * @param onItemBarcode Callback for the barcode icon.
         */
        fun bind(
            row: ShoppingDisplayItem.Item,
            onAddToList: (ShoppingDisplayItem.Item) -> Unit,
            onItemBarcode: (ShoppingDisplayItem.Item) -> Unit
        ) {
            val item = row.item
            binding.tvReplenishItemName.text = item.name
            val drawableRes = BaselineImageMapper.getDrawableResId(item.name)
            binding.ivReplenishItemImage.setImageResource(drawableRes)
            
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

            binding.btnAddToList.setOnClickListener { onAddToList(row) }
            binding.btnItemBarcode.setOnClickListener { onItemBarcode(row) }
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
