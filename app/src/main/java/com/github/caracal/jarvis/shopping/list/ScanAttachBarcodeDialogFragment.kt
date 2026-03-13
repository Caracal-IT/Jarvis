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
        // Try parent fragment chain first
        var p = parentFragment
        while (p != null && p !is ShoppingFragment) {
            p = p.parentFragment
        }
        if (p is ShoppingFragment) return@lazy p.viewModel

        // Fallback: try to locate ShoppingFragment by tag via Activity's supportFragmentManager
        val activity = requireActivity()
        val fm = activity.supportFragmentManager
        val candidate = fm.findFragmentByTag("shopping_list") as? ShoppingFragment
        candidate?.viewModel ?: throw IllegalStateException("ScanAttachBarcodeDialogFragment must be hosted under ShoppingFragment")
    }

    // Keep typed adapters so we can update them without unsafe casts
    private var existingItemsAdapter: ArrayAdapter<String>? = null
    private var newCategoryAdapter: ArrayAdapter<String>? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogScanAttachBarcodeBinding.inflate(layoutInflater)

        // Populate existing items spinner
        val initialItems = shoppingViewModel.shoppingList.value ?: emptyList()
        val itemNames = initialItems.map { it.name }
        existingItemsAdapter = ArrayAdapter(
            requireContext(), R.layout.spinner_item, itemNames
        ).also { it.setDropDownViewResource(R.layout.spinner_dropdown_item) }
        binding.spinnerExistingItems.adapter = existingItemsAdapter

        // Populate categories for new item
        val initialCategories = shoppingViewModel.categories.value ?: emptyList()
        newCategoryAdapter = ArrayAdapter(
            requireContext(), R.layout.spinner_item, initialCategories.map { it.name }
        ).also { it.setDropDownViewResource(R.layout.spinner_dropdown_item) }
        binding.spinnerNewCategory.adapter = newCategoryAdapter

        // Wire buttons
        binding.btnScan.setOnClickListener {
            // Find a fragment to host the scanner dialog - prefer parent fragment, else fallback to activity-tagged ShoppingFragment
            val hostFragment = parentFragment ?: requireActivity().supportFragmentManager.findFragmentByTag("shopping_list")
            val fm = when (hostFragment) {
                is androidx.fragment.app.Fragment -> hostFragment.childFragmentManager
                else -> childFragmentManager
            }
            BarcodeScannerFragment.newInstanceForList()
                .show(fm, "scan_barcode")
            dismiss()
        }

        binding.btnLinkExisting.setOnClickListener {
            val items = shoppingViewModel.shoppingList.value ?: return@setOnClickListener
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
            val categories = shoppingViewModel.categories.value ?: return@setOnClickListener
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
        shoppingViewModel.shoppingList.observe(this) { updated ->
            val names = updated.map { it.name }
            existingItemsAdapter?.let { adapter ->
                adapter.clear(); adapter.addAll(names); adapter.notifyDataSetChanged()
            }
        }
        shoppingViewModel.categories.observe(this) { cats ->
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
