package com.github.caracal.jarvis.postshopping

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.github.caracal.jarvis.databinding.ListItemReceiptItemBinding
import java.util.Locale

/** Adapter that renders the parsed [ReceiptItem] rows below the shop name. */
class ReceiptItemAdapter : ListAdapter<ReceiptItem, ReceiptItemAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(
            ListItemReceiptItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    /** ViewHolder for a single purchased item row. */
    class ViewHolder(private val binding: ListItemReceiptItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        /** Binds the item's name and price to the row; quantity/unit price are kept on [ReceiptItem] but not shown. */
        fun bind(item: ReceiptItem) {
            binding.tvReceiptItemName.text = item.name
            binding.tvReceiptItemPrice.text = String.format(Locale.getDefault(), "%.2f", item.price)
            binding.tvReceiptItemQtyPrice.visibility = View.GONE
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<ReceiptItem>() {
        override fun areItemsTheSame(oldItem: ReceiptItem, newItem: ReceiptItem): Boolean =
            oldItem.name == newItem.name

        override fun areContentsTheSame(oldItem: ReceiptItem, newItem: ReceiptItem): Boolean =
            oldItem == newItem
    }
}
