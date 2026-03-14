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
 *
 * Shows items grouped by category with a floating action button to add new items.
 * Each item exposes a popup menu for rename, remove, and barcode management actions.
 * The ViewModel is shared via the parent [ShoppingFragment].
 */
class ShoppingListFragment : Fragment() {

    private var _binding: ShoppingListFragmentBinding? = null
    private val binding get() = _binding!!

    private val shoppingViewModel by lazy {
        (requireParentFragment() as ShoppingFragment).viewModel
    }

    private val adapter by lazy {
        ShoppingListAdapter(
            onMenuRename = { row -> showRenameDialog(row) },
            onMenuRemove = { row -> shoppingViewModel.removeShoppingItem(row.item.id) },
            onItemBarcode = { row ->
                // Directly open scanner to link barcode to this specific item.
                // NOTE: Use childFragmentManager to listen for results from the scanner dialog.
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
                    .show(childFragmentManager, TAG_SCAN_DIALOG)
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

        // Re-attach the swipe callback.
        val swipeCallback = ShoppingListSwipeCallback(
            adapter = adapter,
            onFullSwipeEdit = { row -> showRenameDialog(row) },
            onFullSwipeDelete = { row -> shoppingViewModel.removeShoppingItem(row.item.id) }
        )
        ItemTouchHelper(swipeCallback).attachToRecyclerView(binding.rvShoppingList)

        shoppingViewModel.shoppingList.observe(viewLifecycleOwner) { items ->
            val displayItems = buildDisplayItems(items)
            adapter.submitList(displayItems)
            binding.rvShoppingList.visibility = 
                if (displayItems.isEmpty()) View.GONE else View.VISIBLE
        }

        // Swap FAB behavior: primary (end) is Add, start FAB is Scan. Keep icons only.
        binding.fabAddItem.setOnClickListener {
            AddItemDialogFragment().show(childFragmentManager, TAG_ADD_DIALOG)
        }

        binding.fabScanBarcode.setOnClickListener {
            BarcodeScannerFragment.newInstanceForList()
                .show(childFragmentManager, TAG_SCAN_DIALOG)
        }
    }

    /**
     * Converts a flat list of [ShoppingItem] into a mixed list of headers and items,
     * inserting a [ShoppingDisplayItem.Header] whenever the category changes.
     *
     * @param items The sorted flat list of shopping items.
     * @return A list suitable for submission to [ShoppingListAdapter].
     */
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

    private fun showRenameDialog(row: ShoppingDisplayItem.Item) {
        EditItemDialogFragment.newInstance(
            itemId = row.item.id,
            itemName = row.item.name,
            itemCategoryId = row.item.categoryId,
            itemBarcodes = row.item.barcodes
        ).show(childFragmentManager, "edit_item")
    }

    fun resetAllItemsSwipeState() {
        for (i in 0 until adapter.itemCount) {
            adapter.notifyItemChanged(i)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val TAG_ADD_DIALOG = "add_item"
        private const val TAG_SCAN_DIALOG = "scan_barcode"
    }
}
