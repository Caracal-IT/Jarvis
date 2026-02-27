package com.github.caracal.jarvis.groceries

import android.graphics.Canvas
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.core.graphics.toColorInt
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.caracal.jarvis.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class InventoryFragment : Fragment() {

    private val adapter by lazy { GroceryAdapter(GroceryRepository.inventoryList) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_inventory, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.rvInventory)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        GroceryRepository.refreshInventoryList()
        recyclerView.adapter = adapter

        // FAB: scan a barcode then ask which inventory item to link it to
        view.findViewById<FloatingActionButton>(R.id.fabScanLink).setOnClickListener {
            (requireActivity() as? BarcodeScannerHost)?.openBarcodeScanner { barcode ->
                showLinkDialog(barcode)
            }
        }

        setupSwipeHandler(recyclerView)
    }

    private fun showLinkDialog(barcode: String) {
        val allItems = GroceryRepository.inventoryList
        val names = allItems.map { getString(it.nameRes) }.toTypedArray()
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.select_item_to_link))
            .setItems(names) { _, which ->
                GroceryRepository.linkBarcode(barcode, allItems[which])
                Toast.makeText(requireContext(), getString(R.string.barcode_linked), Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }

    private fun setupSwipeHandler(recyclerView: RecyclerView) {
        val deleteBackground = "#7A0019".toColorInt().toDrawable()
        val addBackground    = "#1B5E20".toColorInt().toDrawable()
        val deleteIcon = ResourcesCompat.getDrawable(resources, R.drawable.ic_delete, requireContext().theme)!!
        val addIcon    = ResourcesCompat.getDrawable(resources, R.drawable.ic_supplies, requireContext().theme)!!
        val iconMargin = resources.getDimensionPixelSize(R.dimen.swipe_icon_margin)

        val swipeHandler = object : ItemTouchHelper.SimpleCallback(
            0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun getSwipeDirs(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
                val item = GroceryRepository.inventoryList[viewHolder.bindingAdapterPosition]
                val alreadyInGroceries = GroceryRepository.groceryList.any { it.nameRes == item.nameRes }
                return if (alreadyInGroceries) ItemTouchHelper.LEFT else super.getSwipeDirs(recyclerView, viewHolder)
            }

            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.bindingAdapterPosition
                val item = GroceryRepository.inventoryList[position]
                if (direction == ItemTouchHelper.LEFT) {
                    GroceryRepository.removeFromInventory(position)
                    adapter.notifyItemRemoved(position)
                } else {
                    GroceryRepository.moveToGroceries(item, position)
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
        GroceryRepository.refreshInventoryList()
        adapter.notifyItemRangeChanged(0, GroceryRepository.inventoryList.size)
    }
}
