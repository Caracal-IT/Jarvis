package com.github.caracal.jarvis.shopping.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.github.caracal.jarvis.databinding.ShoppingListItemBarcodeBinding

/**
 * Adapter for the barcode list in the Edit Item screen.
 *
 * Displays each barcode with a delete button.
 *
 * @param onDelete Called when the user taps the delete icon for a barcode.
 */
class BarcodeListAdapter(
    private val onDelete: (String) -> Unit
) : ListAdapter<String, BarcodeListAdapter.BarcodeViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BarcodeViewHolder =
        BarcodeViewHolder(
            ShoppingListItemBarcodeBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )

    override fun onBindViewHolder(holder: BarcodeViewHolder, position: Int) =
        holder.bind(getItem(position), onDelete)

    class BarcodeViewHolder(
        private val binding: ShoppingListItemBarcodeBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(barcode: String, onDelete: (String) -> Unit) {
            binding.tvBarcode.text = barcode
            binding.btnDeleteBarcode.setOnClickListener { onDelete(barcode) }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String) = oldItem == newItem
        override fun areContentsTheSame(oldItem: String, newItem: String) = oldItem == newItem
    }
}

