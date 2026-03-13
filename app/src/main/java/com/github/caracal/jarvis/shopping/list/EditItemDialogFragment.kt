package com.github.caracal.jarvis.shopping.list

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.Window
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResultListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.caracal.jarvis.R
import com.github.caracal.jarvis.databinding.ShoppingListEditItemBinding
import com.github.caracal.jarvis.shopping.ShoppingFragment
import com.github.caracal.jarvis.shopping.data.ShoppingCategory

/**
 * Full-screen dialog fragment for editing a Shopping List item.
 *
 * Allows changing the item name, category, and managing multiple barcodes.
 * A barcode scan button opens [BarcodeScannerFragment] and injects the result.
 * The ViewModel is shared via the grandparent [ShoppingFragment].
 */
class EditItemDialogFragment : DialogFragment() {

    private var _binding: ShoppingListEditItemBinding? = null
    private val binding get() = _binding!!

    private val shoppingViewModel by lazy {
        (requireParentFragment().requireParentFragment() as ShoppingFragment).viewModel
    }

    private var categories: List<ShoppingCategory> = emptyList()
    private val barcodes = mutableListOf<String>()
    private val barcodeAdapter: BarcodeListAdapter by lazy {
        BarcodeListAdapter { barcode ->
            barcodes.remove(barcode)
            barcodeAdapter.submitList(barcodes.toList())
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = ShoppingListEditItemBinding.inflate(layoutInflater)

        val dialog = Dialog(requireContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(binding.root)

        val itemId = arguments?.getString(ARG_ITEM_ID) ?: return dialog
        val itemName = arguments?.getString(ARG_ITEM_NAME) ?: return dialog
        val itemCategoryId = arguments?.getString(ARG_ITEM_CATEGORY_ID) ?: return dialog
        val existingBarcodes = arguments?.getStringArrayList(ARG_ITEM_BARCODES) ?: arrayListOf()

        barcodes.clear()
        barcodes.addAll(existingBarcodes)

        // Pre-fill name
        binding.etItemName.setText(itemName)

        // Set up category spinner
        categories = shoppingViewModel.categories.value ?: emptyList()
        val categoryNames = categories.map { it.name }
        val spinnerAdapter = ArrayAdapter(
            requireContext(), R.layout.spinner_item, categoryNames
        ).also { it.setDropDownViewResource(R.layout.spinner_dropdown_item) }
        binding.spinnerCategory.adapter = spinnerAdapter
        val categoryIndex = categories.indexOfFirst { it.id == itemCategoryId }
        if (categoryIndex >= 0) binding.spinnerCategory.setSelection(categoryIndex)

        // Set up barcode RecyclerView
        binding.rvBarcodes.layoutManager = LinearLayoutManager(requireContext())
        binding.rvBarcodes.adapter = barcodeAdapter
        barcodeAdapter.submitList(barcodes.toList())

        // Add barcode manually
        binding.btnAddBarcode.setOnClickListener {
            val barcode = binding.etBarcode.text?.toString()?.trim() ?: ""
            if (barcode.isNotEmpty() && barcode !in barcodes) {
                barcodes.add(barcode)
                barcodeAdapter.submitList(barcodes.toList())
                binding.etBarcode.text?.clear()
            }
        }

        // Scan barcode button — open scanner and wait for result
        binding.btnScanBarcode.setOnClickListener {
            setFragmentResultListener(BarcodeScannerFragment.RESULT_KEY) { _, bundle ->
                val scanned = bundle.getString(BarcodeScannerFragment.RESULT_BARCODE) ?: return@setFragmentResultListener
                if (scanned.isNotEmpty() && scanned !in barcodes) {
                    barcodes.add(scanned)
                    barcodeAdapter.submitList(barcodes.toList())
                    Toast.makeText(requireContext(), getString(R.string.msg_barcode_found, scanned), Toast.LENGTH_SHORT).show()
                }
            }
            BarcodeScannerFragment.newInstanceForEdit()
                .show(childFragmentManager, TAG_SCANNER)
        }

        // Back
        binding.btnBack.setOnClickListener { dismiss() }

        // Save
        binding.btnSave.setOnClickListener {
            val newName = binding.etItemName.text?.toString() ?: ""
            val catIndex = binding.spinnerCategory.selectedItemPosition
            if (catIndex < 0 || catIndex >= categories.size) return@setOnClickListener
            val newCategoryId = categories[catIndex].id
            val updated = shoppingViewModel.updateShoppingItem(itemId, newName, newCategoryId, barcodes.toList())
            if (updated) dismiss()
        }

        // Observe validation errors
        shoppingViewModel.renameItemError.observe(this) { error ->
            binding.tilItemName.error = error
        }

        return dialog
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        shoppingViewModel.clearRenameItemError()
        (parentFragment as? ShoppingListFragment)?.resetAllItemsSwipeState()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_ITEM_ID = "item_id"
        private const val ARG_ITEM_NAME = "item_name"
        private const val ARG_ITEM_CATEGORY_ID = "item_category_id"
        private const val ARG_ITEM_BARCODES = "item_barcodes"
        private const val TAG_SCANNER = "barcode_scanner"

        fun newInstance(
            itemId: String,
            itemName: String,
            itemCategoryId: String,
            itemBarcodes: List<String>
        ): EditItemDialogFragment = EditItemDialogFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_ITEM_ID, itemId)
                putString(ARG_ITEM_NAME, itemName)
                putString(ARG_ITEM_CATEGORY_ID, itemCategoryId)
                putStringArrayList(ARG_ITEM_BARCODES, ArrayList(itemBarcodes))
            }
        }
    }
}
