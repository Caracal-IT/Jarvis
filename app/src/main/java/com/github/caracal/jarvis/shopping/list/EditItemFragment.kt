package com.github.caracal.jarvis.shopping.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.caracal.jarvis.R
import com.github.caracal.jarvis.databinding.ShoppingListEditItemBinding
import com.github.caracal.jarvis.shopping.data.ShoppingCategory
import com.github.caracal.jarvis.shopping.ui.ShoppingViewModel
import com.github.caracal.jarvis.shopping.ui.ShoppingViewModelFactory

/**
 * Fragment for editing an existing Shopping List item.
 */
class EditItemFragment : Fragment() {

    private var _binding: ShoppingListEditItemBinding? = null
    private val binding get() = _binding!!

    private val shoppingViewModel: ShoppingViewModel by activityViewModels {
        ShoppingViewModelFactory(requireContext())
    }

    private lateinit var itemId: String
    private var categories: List<ShoppingCategory> = emptyList()
    private val barcodeAdapter = BarcodeListAdapter { barcode -> removeBarcode(barcode) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        itemId = arguments?.getString(ARG_ITEM_ID) ?: ""
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ShoppingListEditItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val itemName = arguments?.getString(ARG_ITEM_NAME) ?: ""
        val itemCategoryId = arguments?.getString(ARG_ITEM_CATEGORY_ID) ?: ""
        val itemBarcodes = arguments?.getStringArrayList(ARG_ITEM_BARCODES) ?: arrayListOf()

        binding.etItemName.setText(itemName)
        
        binding.rvBarcodes.layoutManager = LinearLayoutManager(requireContext())
        binding.rvBarcodes.adapter = barcodeAdapter
        barcodeAdapter.submitList(itemBarcodes)

        shoppingViewModel.categories.observe(viewLifecycleOwner) { categoryList ->
            categories = categoryList
            val categoryNames = categories.map { it.name }
            binding.spinnerCategory.adapter = ArrayAdapter(
                requireContext(),
                R.layout.spinner_item,
                categoryNames
            ).also { it.setDropDownViewResource(R.layout.spinner_dropdown_item) }

            val catIndex = categories.indexOfFirst { it.id == itemCategoryId }
            if (catIndex >= 0) binding.spinnerCategory.setSelection(catIndex)
        }

        shoppingViewModel.renameItemError.observe(viewLifecycleOwner) { error ->
            binding.tilItemName.error = error
        }

        binding.btnBack.setOnClickListener { parentFragmentManager.popBackStack() }
        
        binding.btnSave.setOnClickListener {
            val newName = binding.etItemName.text?.toString() ?: ""
            val newCatIndex = binding.spinnerCategory.selectedItemPosition
            if (newCatIndex >= 0 && newCatIndex < categories.size) {
                val newCategoryId = categories[newCatIndex].id
                val success = shoppingViewModel.updateShoppingItem(
                    itemId, newName, newCategoryId, barcodeAdapter.currentList
                )
                if (success) parentFragmentManager.popBackStack()
            }
        }

        binding.btnScanBarcode.setOnClickListener {
            childFragmentManager.setFragmentResultListener(BarcodeScannerFragment.RESULT_KEY, viewLifecycleOwner) { _, bundle ->
                val scanned = bundle.getString(BarcodeScannerFragment.RESULT_BARCODE) ?: return@setFragmentResultListener
                addBarcode(scanned)
            }
            BarcodeScannerFragment.newInstanceForEdit()
                .show(childFragmentManager, "scan_barcode_edit")
        }

        binding.btnAddBarcode.setOnClickListener {
            val manual = binding.etBarcode.text?.toString()?.trim() ?: ""
            if (manual.isNotEmpty()) {
                addBarcode(manual)
                binding.etBarcode.text?.clear()
            }
        }
    }

    private fun addBarcode(barcode: String) {
        val current = barcodeAdapter.currentList.toMutableList()
        if (barcode !in current) {
            current.add(barcode)
            barcodeAdapter.submitList(current)
        } else {
            Toast.makeText(requireContext(), getString(R.string.msg_barcode_already_linked, ""), Toast.LENGTH_SHORT).show()
        }
    }

    private fun removeBarcode(barcode: String) {
        val current = barcodeAdapter.currentList.toMutableList()
        current.remove(barcode)
        barcodeAdapter.submitList(current)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        shoppingViewModel.clearRenameItemError()
        _binding = null
    }

    companion object {
        private const val ARG_ITEM_ID = "item_id"
        private const val ARG_ITEM_NAME = "item_name"
        private const val ARG_ITEM_CATEGORY_ID = "item_category_id"
        private const val ARG_ITEM_BARCODES = "item_barcodes"

        fun newInstance(
            itemId: String,
            itemName: String,
            itemCategoryId: String,
            itemBarcodes: List<String>
        ) = EditItemFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_ITEM_ID, itemId)
                putString(ARG_ITEM_NAME, itemName)
                putString(ARG_ITEM_CATEGORY_ID, itemCategoryId)
                putStringArrayList(ARG_ITEM_BARCODES, ArrayList(itemBarcodes))
            }
        }
    }
}
