package com.github.caracal.jarvis.shopping.list

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.caracal.jarvis.R
import com.github.caracal.jarvis.databinding.DialogBarcodesBinding
import com.github.caracal.jarvis.databinding.ListItemBarcodeBinding
import com.github.caracal.jarvis.shopping.ShoppingFragment

/**
 * Dialog fragment for managing barcodes associated with a Shopping List item.
 *
 * Displays the current list of barcodes and allows the user to add new ones
 * or remove existing ones. Changes are reflected live via the shared ViewModel.
 * The ViewModel is shared via the grandparent [ShoppingFragment].
 */
class BarcodeDialogFragment : DialogFragment() {

    private var _binding: DialogBarcodesBinding? = null
    private val binding get() = _binding!!

    private val shoppingViewModel by lazy {
        (requireParentFragment().requireParentFragment() as ShoppingFragment).viewModel
    }

    private lateinit var itemId: String
    private lateinit var barcodeAdapter: BarcodeAdapter

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogBarcodesBinding.inflate(layoutInflater)

        itemId = requireArguments().getString(ARG_ITEM_ID)!!
        val itemName = requireArguments().getString(ARG_ITEM_NAME)!!
        val initialBarcodes = requireArguments().getStringArrayList(ARG_BARCODES) ?: arrayListOf()

        barcodeAdapter = BarcodeAdapter(
            barcodes = initialBarcodes.toMutableList(),
            onRemove = { barcode ->
                shoppingViewModel.removeBarcode(itemId, barcode)
                barcodeAdapter.removeBarcode(barcode)
            }
        )

        binding.rvBarcodes.layoutManager = LinearLayoutManager(requireContext())
        binding.rvBarcodes.adapter = barcodeAdapter

        shoppingViewModel.shoppingList.observe(this) { items ->
            val item = items.find { it.id == itemId }
            if (item != null) {
                barcodeAdapter.updateBarcodes(item.barcodes)
            }
        }

        binding.btnAddBarcode.setOnClickListener {
            val barcode = binding.etBarcode.text?.toString()?.trim() ?: ""
            if (barcode.isNotEmpty()) {
                shoppingViewModel.addBarcode(itemId, barcode)
                binding.etBarcode.text?.clear()
            }
        }

        return AlertDialog.Builder(requireContext())
            .setTitle(itemName)
            .setView(binding.root)
            .setPositiveButton(R.string.action_done, null)
            .create()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * RecyclerView adapter for the barcode list within the dialog.
     *
     * @param barcodes Mutable list of barcode strings to display.
     * @param onRemove Callback invoked when the user taps the remove button for a barcode.
     */
    class BarcodeAdapter(
        private val barcodes: MutableList<String>,
        private val onRemove: (String) -> Unit
    ) : RecyclerView.Adapter<BarcodeAdapter.BarcodeViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BarcodeViewHolder =
            BarcodeViewHolder(
                ListItemBarcodeBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )

        override fun onBindViewHolder(holder: BarcodeViewHolder, position: Int) {
            holder.bind(barcodes[position], onRemove)
        }

        override fun getItemCount(): Int = barcodes.size

        /**
         * Replaces the entire barcode list and notifies the adapter.
         *
         * @param newBarcodes The updated list of barcodes.
         */
        @Suppress("NotifyDataSetChanged")
        fun updateBarcodes(newBarcodes: List<String>) {
            barcodes.clear()
            barcodes.addAll(newBarcodes)
            notifyDataSetChanged()
        }

        /**
         * Removes a single barcode from the list and notifies the adapter.
         *
         * @param barcode The barcode string to remove.
         */
        fun removeBarcode(barcode: String) {
            val index = barcodes.indexOf(barcode)
            if (index >= 0) {
                barcodes.removeAt(index)
                notifyItemRemoved(index)
            }
        }

        /** ViewHolder for a single barcode row. */
        class BarcodeViewHolder(
            private val binding: ListItemBarcodeBinding
        ) : RecyclerView.ViewHolder(binding.root) {

            /**
             * Binds the barcode string and wires the remove button.
             *
             * @param barcode The barcode string to display.
             * @param onRemove Callback for the remove action.
             */
            fun bind(barcode: String, onRemove: (String) -> Unit) {
                binding.tvBarcode.text = barcode
                binding.btnRemoveBarcode.setOnClickListener { onRemove(barcode) }
            }
        }
    }

    companion object {
        private const val ARG_ITEM_ID = "item_id"
        private const val ARG_ITEM_NAME = "item_name"
        private const val ARG_BARCODES = "barcodes"

        /**
         * Creates a new instance with the required arguments.
         *
         * @param itemId The ID of the item whose barcodes are being managed.
         * @param itemName The display name shown as the dialog title.
         * @param barcodes The current list of barcodes for this item.
         */
        fun newInstance(
            itemId: String,
            itemName: String,
            barcodes: List<String>
        ): BarcodeDialogFragment =
            BarcodeDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_ITEM_ID, itemId)
                    putString(ARG_ITEM_NAME, itemName)
                    putStringArrayList(ARG_BARCODES, ArrayList(barcodes))
                }
            }
    }
}
