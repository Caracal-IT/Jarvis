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
import com.github.caracal.jarvis.shopping.data.BaselineData
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
            onItemBarcode = { _ ->
                ScanAttachBarcodeDialogFragment.newInstance()
                    .show(childFragmentManager, "scan_attach_dialog")
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
            onFullSwipeEdit = { row -> showRenameDialog(row) },
            onFullSwipeDelete = { row -> shoppingViewModel.removeShoppingItem(row.item.id) }
        )
        ItemTouchHelper(swipeCallback).attachToRecyclerView(binding.rvShoppingList)

        shoppingViewModel.shoppingList.observe(viewLifecycleOwner) { items ->
            val displayItems = buildDisplayItems(items)
            adapter.submitList(displayItems)
            binding.tvEmptyState.visibility =
                if (displayItems.isEmpty()) View.VISIBLE else View.GONE
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
                val category = BaselineData.categoryById(item.categoryId) ?: continue
                result.add(ShoppingDisplayItem.Header(category))
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
