package com.github.caracal.jarvis.shopping.list

import android.app.Dialog
import android.os.Bundle
import android.view.Window
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.github.caracal.jarvis.R
import com.github.caracal.jarvis.databinding.ShoppingListFragmentBarcodeResultBinding
import com.github.caracal.jarvis.shopping.ShoppingFragment
import com.github.caracal.jarvis.shopping.data.ShoppingCategory
import com.github.caracal.jarvis.shopping.data.ShoppingItem

/**
 * Screen shown when a scanned barcode is not linked to any existing Shopping List item.
 *
 * The user can either:
 * 1. Link the barcode to an existing item in the Shopping List.
 * 2. Create a new item with that barcode attached.
 */
class BarcodeResultFragment : DialogFragment() {

    private var _binding: ShoppingListFragmentBarcodeResultBinding? = null
    private val binding get() = _binding!!

    private val shoppingViewModel by lazy {
        (requireParentFragment() as ShoppingFragment).viewModel
    }

    private var shoppingItems: List<ShoppingItem> = emptyList()
    private var categories: List<ShoppingCategory> = emptyList()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = ShoppingListFragmentBarcodeResultBinding.inflate(layoutInflater)
        val dialog = Dialog(requireContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(binding.root)

        val barcode = arguments?.getString(ARG_BARCODE) ?: run { dismiss(); return dialog }

        binding.tvScannedBarcode.text = getString(R.string.msg_barcode_found, barcode)

        // Populate existing items spinner
        shoppingItems = shoppingViewModel.shoppingList.value ?: emptyList()
        val itemNames = shoppingItems.map { it.name }
        binding.spinnerExistingItem.adapter = ArrayAdapter(
            requireContext(), R.layout.spinner_item, itemNames
        ).also { it.setDropDownViewResource(R.layout.spinner_dropdown_item) }

        // Populate new category spinner
        categories = shoppingViewModel.categories.value ?: emptyList()
        binding.spinnerNewCategory.adapter = ArrayAdapter(
            requireContext(), R.layout.spinner_item, categories.map { it.name }
        ).also { it.setDropDownViewResource(R.layout.spinner_dropdown_item) }

        binding.btnBack.setOnClickListener { dismiss() }

        // Link barcode to existing item
        binding.btnLinkItem.setOnClickListener {
            val index = binding.spinnerExistingItem.selectedItemPosition
            if (index < 0 || index >= shoppingItems.size) return@setOnClickListener
            val item = shoppingItems[index]
            val newBarcodes = (item.barcodes + barcode).distinct()
            val updated = shoppingViewModel.updateShoppingItem(
                item.id, item.name, item.categoryId, newBarcodes
            )
            if (updated) {
                Toast.makeText(requireContext(), getString(R.string.msg_barcode_linked, item.name), Toast.LENGTH_SHORT).show()
                dismiss()
            }
        }

        // Add new item with barcode
        binding.btnAddNewItem.setOnClickListener {
            val name = binding.etNewItemName.text?.toString()?.trim() ?: ""
            val catIndex = binding.spinnerNewCategory.selectedItemPosition
            if (name.isEmpty() || catIndex < 0 || catIndex >= categories.size) return@setOnClickListener
            val categoryId = categories[catIndex].id
            val newItem = shoppingViewModel.addShoppingItemWithBarcode(name, categoryId, barcode)
            if (newItem) {
                Toast.makeText(requireContext(), getString(R.string.msg_item_added_to_list, name), Toast.LENGTH_SHORT).show()
                dismiss()
            }
        }

        return dialog
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_BARCODE = "barcode"

        fun newInstance(barcode: String) = BarcodeResultFragment().apply {
            arguments = Bundle().apply { putString(ARG_BARCODE, barcode) }
        }
    }
}

