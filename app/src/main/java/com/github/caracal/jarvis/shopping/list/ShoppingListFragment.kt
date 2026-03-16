package com.github.caracal.jarvis.shopping.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.caracal.jarvis.databinding.ShoppingListFragmentBinding
import com.github.caracal.jarvis.shopping.ShoppingFragment
import com.github.caracal.jarvis.shopping.data.ShoppingItem
import com.github.caracal.jarvis.shopping.ui.ShoppingDisplayItem

/**
 * Fragment displaying the user's current Shopping List.
 */
class ShoppingListFragment : Fragment() {

    private var _binding: ShoppingListFragmentBinding? = null
    private val binding get() = _binding!!

    private val shoppingViewModel by lazy {
        (requireParentFragment() as ShoppingFragment).viewModel
    }

    private val adapter by lazy {
        ShoppingListAdapter(
            onMenuRename = { row -> showRenamePage(row) },
            onMenuRemove = { row -> shoppingViewModel.removeShoppingItem(row.item.id) },
            onItemBarcode = { row ->
                // Use Fragment Result API to listen for the scan result from the scanner page.
                childFragmentManager.setFragmentResultListener(BarcodeScannerFragment.RESULT_KEY, viewLifecycleOwner) { _, bundle ->
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
                    .show(childFragmentManager, TAG_SCAN_PAGE)
            }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ShoppingListFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvShoppingList.layoutManager = LinearLayoutManager(requireContext())
        binding.rvShoppingList.adapter = adapter

        val swipeCallback = ShoppingListSwipeCallback(
            adapter = adapter,
            onFullSwipeEdit = { row -> showRenamePage(row) },
            onFullSwipeDelete = { row -> shoppingViewModel.removeShoppingItem(row.item.id) }
        )
        ItemTouchHelper(swipeCallback).attachToRecyclerView(binding.rvShoppingList)

        shoppingViewModel.shoppingList.observe(viewLifecycleOwner) { items ->
            val displayItems = buildDisplayItems(items)
            adapter.submitList(displayItems)
            binding.rvShoppingList.visibility = 
                if (displayItems.isEmpty()) View.GONE else View.VISIBLE
        }

        binding.fabAddItem.setOnClickListener {
            (requireParentFragment() as ShoppingFragment).navigateTo(AddItemFragment())
        }

        binding.fabScanBarcode.setOnClickListener {
            // Scanner remains a full-screen dialog as it requires a specific lifecycle/camera setup,
            // but its results are handled as a navigation flow.
            BarcodeScannerFragment.newInstanceForList()
                .show(childFragmentManager, TAG_SCAN_PAGE)
        }
    }

    private fun buildDisplayItems(items: List<ShoppingItem>): List<ShoppingDisplayItem> {
        val result = mutableListOf<ShoppingDisplayItem>()
        var lastCategoryId: String? = null

        for (item in items) {
            if (item.categoryId != lastCategoryId) {
                val category = shoppingViewModel.categories.value?.find { it.id == item.categoryId }
                category?.let { result.add(ShoppingDisplayItem.Header(it)) }
                lastCategoryId = item.categoryId
            }
            result.add(ShoppingDisplayItem.Item(item))
        }
        return result
    }

    private fun showRenamePage(row: ShoppingDisplayItem.Item) {
        val fragment = EditItemFragment.newInstance(
            itemId = row.item.id,
            itemName = row.item.name,
            itemCategoryId = row.item.categoryId,
            itemBarcodes = row.item.barcodes
        )
        (requireParentFragment() as ShoppingFragment).navigateTo(fragment)
    }

    /**
     * Resets the swipe state of all items in the list.
     */
    fun resetAllItemsSwipeState() {
        adapter.notifyItemRangeChanged(0, adapter.itemCount)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val TAG_SCAN_PAGE = "scan_page"
    }
}
