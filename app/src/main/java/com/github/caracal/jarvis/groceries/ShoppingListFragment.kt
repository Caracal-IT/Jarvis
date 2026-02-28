package com.github.caracal.jarvis.groceries

import android.graphics.Canvas
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.core.graphics.toColorInt
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.caracal.jarvis.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

class ShoppingListFragment : Fragment() {

    private lateinit var adapter: GroceryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_shopping_list, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.rvGroceries)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        adapter = GroceryAdapter(GroceryRepository.groceryList) { item, position ->
            showRenameDialog(requireContext(), item, position) { pos ->
                adapter.notifyItemChanged(pos)
            }
        }
        recyclerView.adapter = adapter

        view.findViewById<FloatingActionButton>(R.id.fabScanGrocery).setOnClickListener {
            (requireActivity() as? BarcodeScannerHost)?.openBarcodeScanner { barcode ->
                handleBarcodeScan(barcode)
            }
        }

        setupSwipeHandler(recyclerView)
    }

    private fun handleBarcodeScan(barcode: String) {
        // 1. Barcode already linked to a base item — add directly to shopping list
        val baseItem = GroceryRepository.findByBarcode(barcode)
        if (baseItem != null) {
            val added = GroceryRepository.addToGroceriesByBarcode(barcode)
            val msg = if (added) getString(R.string.barcode_added)
                      else getString(R.string.barcode_already_in_list)
            Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
            if (added) adapter.notifyItemInserted(GroceryRepository.groceryList.size - 1)
            return
        }

        // 2. Barcode not linked — search online
        Toast.makeText(requireContext(), getString(R.string.searching_product), Toast.LENGTH_SHORT).show()
        viewLifecycleOwner.lifecycleScope.launch {
            val product = ProductLookupService.lookup(barcode)
            if (product != null) {
                // 2a. Check if this product name already exists in base items (different barcode)
                val existingByName = GroceryRepository.findByName(product.name) { resId ->
                    getString(resId)
                }
                if (existingByName != null) {
                    // Link this barcode to the existing item, then add to shopping list
                    GroceryRepository.linkBarcode(barcode, existingByName)
                    val added = GroceryRepository.addToGroceriesByBarcode(barcode)
                    val msg = if (added) getString(R.string.product_found, existingByName.getName { getString(it) })
                              else getString(R.string.barcode_already_in_list)
                    Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                    if (added) adapter.notifyItemInserted(GroceryRepository.groceryList.size - 1)
                } else {
                    // 2b. Genuinely new product — create, add to base items + shopping list
                    val iconSet = IconGenerator.resolve(product.name, product.category)
                    val item = GroceryItem.Dynamic(
                        name = product.name,
                        iconRes = iconSet.iconRes,
                        iconBgRes = iconSet.bgRes,
                        barcodes = mutableSetOf(barcode)
                    )
                    GroceryRepository.addDynamicItem(item, addToGroceries = true)
                    adapter.notifyItemInserted(GroceryRepository.groceryList.size - 1)
                    Toast.makeText(requireContext(),
                        getString(R.string.product_found, product.name),
                        Toast.LENGTH_SHORT).show()
                }
            } else {
                // 3. Not found anywhere — open OCR scanner to read the label
                Toast.makeText(requireContext(),
                    getString(R.string.product_not_found_try_ocr),
                    Toast.LENGTH_SHORT).show()
                openOcrThenDialog(barcode)
            }
        }
    }

    private fun openOcrThenDialog(barcode: String) {
        (requireActivity() as? BarcodeScannerHost)?.openOcrScanner { ocrText ->
            if (ocrText.isNotBlank()) {
                Toast.makeText(requireContext(),
                    getString(R.string.ocr_result_prefilled),
                    Toast.LENGTH_SHORT).show()
            }
            showAddItemDialog(barcode, prefilledName = ocrText.ifBlank { null })
        }
    }

    private fun showAddItemDialog(barcode: String, prefilledName: String? = null) {
        val dialog = AddItemDialog().apply {
            this.barcode = barcode
            this.prefilledName = prefilledName
            this.onItemAdded = {
                adapter.notifyItemInserted(GroceryRepository.groceryList.size - 1)
            }
        }
        dialog.show(parentFragmentManager, "add_item")
    }

    private fun setupSwipeHandler(recyclerView: RecyclerView) {
        val deleteBackground = "#7A0019".toColorInt().toDrawable()
        val addBackground    = "#1B5E20".toColorInt().toDrawable()
        val deleteIcon = ResourcesCompat.getDrawable(resources, R.drawable.ic_delete, requireContext().theme)!!
        val addIcon    = ResourcesCompat.getDrawable(resources, R.drawable.ic_inventory, requireContext().theme)!!
        val iconMargin = resources.getDimensionPixelSize(R.dimen.swipe_icon_margin)

        val swipeHandler = object : ItemTouchHelper.SimpleCallback(
            0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun getSwipeDirs(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
                val item = GroceryRepository.groceryList[viewHolder.bindingAdapterPosition]
                val alreadyInBaseItems = GroceryRepository.baseItemsList.any { base ->
                    (item.barcodes.isNotEmpty() && item.barcodes.any { base.barcodes.contains(it) }) ||
                    (item is GroceryItem.Static && base is GroceryItem.Static && base.nameRes == item.nameRes) ||
                    (item is GroceryItem.Dynamic && base is GroceryItem.Dynamic && base.name.equals(item.name, ignoreCase = true))
                }
                return if (alreadyInBaseItems) ItemTouchHelper.LEFT else super.getSwipeDirs(recyclerView, viewHolder)
            }

            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.bindingAdapterPosition
                val item = GroceryRepository.groceryList[position]
                if (direction == ItemTouchHelper.LEFT) {
                    GroceryRepository.removeFromGroceries(item, position)
                    adapter.notifyItemRemoved(position)
                } else {
                    GroceryRepository.moveToBaseItems(item, position)
                    adapter.notifyItemRemoved(position)
                }
            }

            override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
                val itemView = viewHolder.itemView
                val isSwipingLeft = dX < 0
                val bg   = if (isSwipingLeft) deleteBackground else addBackground
                val icon = if (isSwipingLeft) deleteIcon else addIcon
                val iconTop    = itemView.top + (itemView.height - icon.intrinsicHeight) / 2
                val iconBottom = iconTop + icon.intrinsicHeight
                if (isSwipingLeft) {
                    bg.setBounds(itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom)
                    bg.draw(c)
                    if (dX < -iconMargin * 2) {
                        icon.setBounds(itemView.right - iconMargin - icon.intrinsicWidth, iconTop, itemView.right - iconMargin, iconBottom)
                        icon.draw(c)
                    }
                } else {
                    bg.setBounds(itemView.left, itemView.top, itemView.left + dX.toInt(), itemView.bottom)
                    bg.draw(c)
                    if (dX > iconMargin * 2) {
                        icon.setBounds(itemView.left + iconMargin, iconTop, itemView.left + iconMargin + icon.intrinsicWidth, iconBottom)
                        icon.draw(c)
                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
        }
        ItemTouchHelper(swipeHandler).attachToRecyclerView(recyclerView)
    }

    override fun onResume() {
        super.onResume()
        adapter.notifyItemRangeChanged(0, GroceryRepository.groceryList.size)
    }
}

