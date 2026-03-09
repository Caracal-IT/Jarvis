package com.github.caracal.jarvis.shopping.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.github.caracal.jarvis.R
import com.github.caracal.jarvis.databinding.ListItemCategoryHeaderBinding
import com.github.caracal.jarvis.databinding.ListItemShoppingSwipeableBinding
import com.github.caracal.jarvis.shopping.ui.ShoppingDisplayItem

/**
 * Adapter for the Shopping List RecyclerView.
 *
 * Renders both category header rows and shopping item rows. Item rows expose a popup
 * menu with rename and remove options.
 *
 * @param onMenuRename Callback invoked when the user selects "Rename" for an item.
 * @param onMenuRemove Callback invoked when the user selects "Remove" for an item.
 */
class ShoppingListAdapter(
    private val onMenuRename: (ShoppingDisplayItem.Item) -> Unit,
    private val onMenuRemove: (ShoppingDisplayItem.Item) -> Unit
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
                ListItemShoppingSwipeableBinding.inflate(inflater, parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val row = getItem(position)) {
            is ShoppingDisplayItem.Header -> (holder as HeaderViewHolder).bind(row)
            is ShoppingDisplayItem.Item -> (holder as ItemViewHolder).bind(
                row, onMenuRename, onMenuRemove
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

    /** ViewHolder for shopping item rows with swipe-to-reveal actions. */
    class ItemViewHolder(
        private val binding: ListItemShoppingSwipeableBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        /**
         * Binds the item data and wires the action button callbacks.
         *
         * @param row The display item containing the [com.github.caracal.jarvis.shopping.data.ShoppingItem].
         * @param onMenuRename Callback for rename action.
         * @param onMenuRemove Callback for remove/delete action.
         */
        fun bind(
            row: ShoppingDisplayItem.Item,
            onMenuRename: (ShoppingDisplayItem.Item) -> Unit,
            onMenuRemove: (ShoppingDisplayItem.Item) -> Unit
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

            // Reset recycled row state so actions are not visible when closed.
            resetClosedState()

            // Wire up action buttons on revealed background
            binding.btnRename.setOnClickListener {
                onMenuRename(row)
                resetClosedState()
            }
            binding.btnDelete.setOnClickListener {
                onMenuRemove(row)
            }

            // Add double-tap listener to reset swipe state
            var lastTapTime = 0L
            binding.foreground.setOnClickListener {
                val currentTime = System.currentTimeMillis()
                if (currentTime - lastTapTime < 300) {
                    // Double-tap detected - reset swipe state
                    resetClosedState()
                    lastTapTime = 0L
                } else {
                    lastTapTime = currentTime
                }
            }
        }

        /** Returns the foreground view that can be swiped. */
        fun getForeground(): View = binding.foreground


        /** Sets the background color based on swipe direction (green for left actions, red for right action). */
        fun setBackgroundColor(colorRes: Int) {
            binding.actionsBackground.setBackgroundColor(
                binding.root.context.getColor(colorRes)
            )
        }

        /** Shows or hides the left action buttons (edit) based on visibility. */
        fun showLeftActions(visible: Boolean) {
            binding.leftActions.visibility = if (visible) View.VISIBLE else View.INVISIBLE
            binding.leftActions.alpha = if (visible) 1f else 0f
            binding.leftActions.isEnabled = visible
            binding.btnRename.isEnabled = visible
        }

        /** Shows or hides the right action button (delete) based on visibility. */
        fun showRightActions(visible: Boolean) {
            binding.btnDelete.visibility = if (visible) View.VISIBLE else View.INVISIBLE
            binding.btnDelete.alpha = if (visible) 1f else 0f
            binding.btnDelete.isEnabled = visible
        }

        /** Shows left actions with progressive alpha for small swipe reveals. */
        fun setLeftRevealFraction(fraction: Float) {
            val clamped = fraction.coerceIn(0f, 1f)
            val visible = clamped > 0f
            binding.leftActions.visibility = if (visible) View.VISIBLE else View.INVISIBLE
            binding.leftActions.alpha = clamped
            binding.leftActions.isEnabled = visible
            binding.btnRename.isEnabled = visible
        }

        /** Shows right action with progressive alpha for small swipe reveals. */
        fun setRightRevealFraction(fraction: Float) {
            val clamped = fraction.coerceIn(0f, 1f)
            val visible = clamped > 0f
            binding.btnDelete.visibility = if (visible) View.VISIBLE else View.INVISIBLE
            binding.btnDelete.alpha = clamped
            binding.btnDelete.isEnabled = visible
        }

        /** Resets the item to closed position and hides all action buttons. */
        fun resetClosedState() {
            binding.foreground.translationX = 0f
            showLeftActions(false)
            showRightActions(false)
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
