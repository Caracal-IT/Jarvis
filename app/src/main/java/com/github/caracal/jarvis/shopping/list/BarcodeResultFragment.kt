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
 * Screen shown after a barcode scan from the Shopping List.
 *
 * The user can either:
 * 1. Link the barcode to an existing item in the Replenish List or Shopping List.
 * 2. Create a new item (which is also added to the Replenish List).
 */
class BarcodeResultFragment : DialogFragment() {

    private var _binding: ShoppingListFragmentBarcodeResultBinding? = null
    private val binding get() = _binding!!

    private val shoppingViewModel by lazy {
        (requireParentFragment().requireParentFragment() as ShoppingFragment).viewModel
    }

    private var allItems: List<ShoppingItem> = emptyList()
    private var categories: List<ShoppingCategory> = emptyList()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = ShoppingListFragmentBarcodeResultBinding.inflate(layoutInflater)
        val dialog = Dialog(requireContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(binding.root)

        val barcode = arguments?.getString(ARG_BARCODE) ?: run { dismiss(); return dialog }
        val matchedItemId = arguments?.getString(ARG_MATCHED_ITEM_ID)

        binding.tvScannedBarcode.text = getString(R.string.msg_scanned_barcode, barcode)

        // Combine replenish list and shopping list into a unique list sorted alphabetically.
        val shoppingList = shoppingViewModel.shoppingList.value ?: emptyList()
        val replenishList = shoppingViewModel.replenishList.value ?: emptyList()
        
        allItems = (shoppingList + replenishList)
            .distinctBy { it.id }
            .sortedBy { it.name.lowercase() }

        val itemNames = allItems.map { it.name }
        binding.spinnerExistingItem.adapter = ArrayAdapter(
            requireContext(),
            R.layout.spinner_item,
            itemNames
        ).also { it.setDropDownViewResource(R.layout.spinner_dropdown_item) }

        // Populate new category spinner.
        categories = shoppingViewModel.categories.value ?: emptyList()
        binding.spinnerNewCategory.adapter = ArrayAdapter(
            requireContext(),
            R.layout.spinner_item,
            categories.map { it.name }
        ).also { it.setDropDownViewResource(R.layout.spinner_dropdown_item) }

        configureHeaderAndSelection(matchedItemId)

        binding.btnBack.setOnClickListener { dismiss() }

        // Link barcode to existing item and ensure it's on the shopping list.
        binding.btnLinkItem.setOnClickListener {
            val index = binding.spinnerExistingItem.selectedItemPosition
            if (index < 0 || index >= allItems.size) return@setOnClickListener
            val item = allItems[index]
            val newBarcodes = (item.barcodes + barcode).distinct()
            val updated = shoppingViewModel.updateShoppingItem(
                item.id,
                item.name,
                item.categoryId,
                newBarcodes
            )
            if (updated) {
                // If it's not on the list, move it to the shopping list.
                // addBaselineItemToShoppingList handles setting isOnShoppingList = true for any item by ID now.
                shoppingViewModel.addBaselineItemToShoppingList(item.id)
                Toast.makeText(
                    requireContext(),
                    getString(R.string.msg_barcode_linked, item.name),
                    Toast.LENGTH_SHORT
                ).show()
                dismiss()
            }
        }

        // Add new item with barcode.
        binding.btnAddNewItem.setOnClickListener {
            val name = binding.etNewItemName.text?.toString()?.trim() ?: ""
            val catIndex = binding.spinnerNewCategory.selectedItemPosition
            if (name.isEmpty() || catIndex < 0 || catIndex >= categories.size) return@setOnClickListener
            val categoryId = categories[catIndex].id
            val added = shoppingViewModel.addShoppingItemWithBarcode(name, categoryId, barcode)
            if (added) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.msg_item_added_to_list, name),
                    Toast.LENGTH_SHORT
                ).show()
                dismiss()
            }
        }

        return dialog
    }

    private fun configureHeaderAndSelection(matchedItemId: String?) {
        if (allItems.isEmpty()) {
            binding.spinnerExistingItem.isEnabled = false
            binding.btnLinkItem.isEnabled = false
            binding.tvTitle.text = getString(R.string.title_barcode_not_found)
            binding.tvBarcodeStatus.text = getString(R.string.msg_no_items_to_link)
            return
        }

        if (matchedItemId == null) {
            binding.tvTitle.text = getString(R.string.title_barcode_not_found)
            binding.tvBarcodeStatus.text = getString(R.string.msg_barcode_not_linked_yet)
            return
        }

        val matchedIndex = allItems.indexOfFirst { it.id == matchedItemId }
        if (matchedIndex >= 0) {
            val matchedItem = allItems[matchedIndex]
            binding.spinnerExistingItem.setSelection(matchedIndex)
            binding.tvTitle.text = getString(R.string.title_barcode_found)
            binding.tvBarcodeStatus.text = getString(R.string.msg_barcode_already_linked, matchedItem.name)
        } else {
            binding.tvTitle.text = getString(R.string.title_barcode_not_found)
            binding.tvBarcodeStatus.text = getString(R.string.msg_barcode_not_linked_yet)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_BARCODE = "barcode"
        private const val ARG_MATCHED_ITEM_ID = "matched_item_id"

        fun newInstance(barcode: String, matchedItemId: String? = null) = BarcodeResultFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_BARCODE, barcode)
                putString(ARG_MATCHED_ITEM_ID, matchedItemId)
            }
        }
    }
}
