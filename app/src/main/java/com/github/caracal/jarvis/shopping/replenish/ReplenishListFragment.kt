package com.github.caracal.jarvis.shopping.replenish

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.caracal.jarvis.R
import com.github.caracal.jarvis.databinding.ShoppingReplenishFragmentBinding
import com.github.caracal.jarvis.shopping.ShoppingFragment
import com.github.caracal.jarvis.shopping.data.BaselineData
import com.github.caracal.jarvis.shopping.data.ShoppingItem
import com.github.caracal.jarvis.shopping.list.BarcodeScannerFragment
import com.github.caracal.jarvis.shopping.ui.ShoppingDisplayItem

/**
 * Fragment displaying the baseline Replenish List.
 *
 * Shows all baseline items grouped by category. Tapping "Add" on any item adds it to
 * the Shopping List and shows a toast confirmation. The ViewModel is shared via the
 * parent [ShoppingFragment].
 */
class ReplenishListFragment : Fragment() {

    private var _binding: ShoppingReplenishFragmentBinding? = null
    private val binding get() = _binding!!

    private val shoppingViewModel by lazy {
        (requireParentFragment() as ShoppingFragment).viewModel
    }

    private val adapter by lazy {
        ReplenishListAdapter(
            onAddToList = { row: ShoppingDisplayItem.Item ->
                val added = shoppingViewModel.addBaselineItemToShoppingList(row.item.id)
                val message = if (added) {
                    getString(R.string.msg_item_added_to_list, row.item.name)
                } else {
                    getString(R.string.msg_item_already_in_list, row.item.name)
                }
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            },
            onItemBarcode = { row: ShoppingDisplayItem.Item ->
                // Directly open scanner to link barcode to this specific replenish item.
                setFragmentResultListener(BarcodeScannerFragment.RESULT_KEY) { _, bundle ->
                    val scanned = bundle.getString(BarcodeScannerFragment.RESULT_BARCODE) ?: return@setFragmentResultListener
                    val updatedBarcodes = (row.item.barcodes + scanned).distinct()
                    shoppingViewModel.updateShoppingItem(
                        row.item.id,
                        row.item.name,
                        row.item.categoryId,
                        updatedBarcodes
                    )
                }
                BarcodeScannerFragment.newInstanceForEdit()
                    .show(childFragmentManager, "scan_barcode_replenish")
            }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ShoppingReplenishFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvReplenishList.layoutManager = LinearLayoutManager(requireContext())
        binding.rvReplenishList.adapter = adapter

        // Setup double-tap gesture to add items
        val doubleTapListener = DoubleTapItemTouchListener(binding.rvReplenishList) { position ->
            val displayItem = adapter.currentList.getOrNull(position)
            if (displayItem is ShoppingDisplayItem.Item) {
                val added = shoppingViewModel.addBaselineItemToShoppingList(displayItem.item.id)
                val message = if (added) {
                    getString(R.string.msg_item_added_to_list, displayItem.item.name)
                } else {
                    getString(R.string.msg_item_already_in_list, displayItem.item.name)
                }
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        }
        binding.rvReplenishList.addOnItemTouchListener(doubleTapListener)

        shoppingViewModel.replenishList.observe(viewLifecycleOwner) { items ->
            adapter.submitList(buildDisplayItems(items))
        }
    }

    /**
     * Converts a flat list of baseline [ShoppingItem] into a mixed list of headers and items,
     * inserting a [ShoppingDisplayItem.Header] whenever the category changes.
     *
     * @param items The sorted flat list of baseline items.
     * @return A list suitable for submission to [ReplenishListAdapter].
     */
    private fun buildDisplayItems(items: List<ShoppingItem>): List<ShoppingDisplayItem> {
        val result = mutableListOf<ShoppingDisplayItem>()
        var lastCategoryId: String? = null
        for (item in items) {
            if (item.categoryId != lastCategoryId) {
                val category = BaselineData.categoryById(item.categoryId) ?: continue
                result.add(ShoppingDisplayItem.Header(category))
                lastCategoryId = item.categoryId
            }
            result.add(ShoppingDisplayItem.Item(item))
        }
        return result
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
