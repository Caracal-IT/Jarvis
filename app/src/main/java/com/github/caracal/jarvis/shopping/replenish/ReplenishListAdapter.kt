package com.github.caracal.jarvis.shopping.replenish

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.github.caracal.jarvis.databinding.ListItemCategoryHeaderBinding
import com.github.caracal.jarvis.databinding.ShoppingReplenishItemBinding
import com.github.caracal.jarvis.shopping.ui.ShoppingDisplayItem

/**
 * Adapter for the Replenish List RecyclerView.
 *
 * Renders baseline items grouped by category header. Each item row has an "Add" button
 * that triggers the [onAddToList] callback.
 *
 * @param onAddToList Callback invoked when the user taps "Add" for a baseline item.
 */
class ReplenishListAdapter(
    private val onAddToList: (ShoppingDisplayItem.Item) -> Unit
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
            is ShoppingDisplayItem.Item -> (holder as ItemViewHolder).bind(row, onAddToList)
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
         * Binds the item name, image, and "Add" button.
         *
         * @param row The display item containing the baseline [com.github.caracal.jarvis.shopping.data.ShoppingItem].
         * @param onAddToList Callback invoked when the "Add" button is tapped.
         */
        fun bind(
            row: ShoppingDisplayItem.Item,
            onAddToList: (ShoppingDisplayItem.Item) -> Unit
        ) {
            val item = row.item
            binding.tvReplenishItemName.text = item.name
            val drawableRes = BaselineImageMapper.getDrawableResId(item.name)
            binding.ivReplenishItemImage.setImageResource(drawableRes)
            binding.btnAddToList.setOnClickListener { onAddToList(row) }
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
