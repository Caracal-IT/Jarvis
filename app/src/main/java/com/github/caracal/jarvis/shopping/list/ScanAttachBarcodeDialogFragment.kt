package com.github.caracal.jarvis.shopping.list

import android.app.Dialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.github.caracal.jarvis.R
import com.github.caracal.jarvis.databinding.DialogScanAttachBarcodeBinding
import com.github.caracal.jarvis.shopping.ShoppingFragment

class ScanAttachBarcodeDialogFragment : DialogFragment() {

    private var _binding: DialogScanAttachBarcodeBinding? = null
    private val binding get() = _binding!!

    private val shoppingViewModel by lazy {
        (requireParentFragment() as ShoppingFragment).viewModel
    }

    // Keep typed adapters so we can update them without unsafe casts
    private var existingItemsAdapter: ArrayAdapter<String>? = null
    private var newCategoryAdapter: ArrayAdapter<String>? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogScanAttachBarcodeBinding.inflate(layoutInflater)

        // Populate existing items spinner
        val items = shoppingViewModel.shoppingList.value ?: emptyList()
        val itemNames = items.map { it.name }
        existingItemsAdapter = ArrayAdapter(
            requireContext(), R.layout.spinner_item, itemNames
        ).also { it.setDropDownViewResource(R.layout.spinner_dropdown_item) }
        binding.spinnerExistingItems.adapter = existingItemsAdapter

        // Populate categories for new item
        val categories = shoppingViewModel.categories.value ?: emptyList()
        newCategoryAdapter = ArrayAdapter(
            requireContext(), R.layout.spinner_item, categories.map { it.name }
        ).also { it.setDropDownViewResource(R.layout.spinner_dropdown_item) }
        binding.spinnerNewCategory.adapter = newCategoryAdapter

        // Wire buttons
        binding.btnScan.setOnClickListener {
            BarcodeScannerFragment.newInstanceForList()
                .show(requireParentFragment().childFragmentManager, "scan_barcode")
            dismiss()
        }

        binding.btnLinkExisting.setOnClickListener {
            val index = binding.spinnerExistingItems.selectedItemPosition
            if (index < 0 || index >= items.size) return@setOnClickListener
            val barcode = binding.etExistingBarcode.text?.toString()?.trim() ?: ""
            if (barcode.isEmpty()) return@setOnClickListener
            val item = items[index]
            val newBarcodes = (item.barcodes + barcode).distinct()
            val updated = shoppingViewModel.updateShoppingItem(item.id, item.name, item.categoryId, newBarcodes)
            if (updated) {
                Toast.makeText(requireContext(), getString(R.string.msg_barcode_linked, item.name), Toast.LENGTH_SHORT).show()
                dismiss()
            } else {
                Toast.makeText(requireContext(), getString(R.string.action_manage_barcodes), Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnAddNewWithBarcode.setOnClickListener {
            val name = binding.etNewItemName.text?.toString()?.trim() ?: ""
            val catIndex = binding.spinnerNewCategory.selectedItemPosition
            val barcode = binding.etNewBarcode.text?.toString()?.trim() ?: ""
            if (name.isEmpty() || barcode.isEmpty() || catIndex < 0 || catIndex >= categories.size) return@setOnClickListener
            val categoryId = categories[catIndex].id
            val added = shoppingViewModel.addShoppingItemWithBarcode(name, categoryId, barcode)
            if (added) {
                Toast.makeText(requireContext(), getString(R.string.msg_item_added_to_list, name), Toast.LENGTH_SHORT).show()
                dismiss()
            } else {
                Toast.makeText(requireContext(), "Failed to add item (duplicate?).", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnClose.setOnClickListener { dismiss() }

        // Observe lists to update spinners if data changes
        shoppingViewModel.shoppingList.observe(viewLifecycleOwner) { updated ->
            val names = updated.map { it.name }
            existingItemsAdapter?.let { adapter ->
                adapter.clear(); adapter.addAll(names); adapter.notifyDataSetChanged()
            }
        }
        shoppingViewModel.categories.observe(viewLifecycleOwner) { cats ->
            val names = cats.map { it.name }
            newCategoryAdapter?.let { adapter ->
                adapter.clear(); adapter.addAll(names); adapter.notifyDataSetChanged()
            }
        }

        return AlertDialog.Builder(requireContext())
            .setTitle(R.string.title_scan_barcode)
            .setView(binding.root)
            .create()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = ScanAttachBarcodeDialogFragment()
    }
}
