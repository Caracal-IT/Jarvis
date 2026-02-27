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
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.caracal.jarvis.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class GroceriesListFragment : Fragment() {

    private val adapter by lazy { GroceryAdapter(GroceryRepository.groceryList) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_groceries_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.rvGroceries)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        // FAB: scan barcode to add item to grocery list
        view.findViewById<FloatingActionButton>(R.id.fabScanGrocery).setOnClickListener {
            (requireActivity() as? BarcodeScannerHost)?.openBarcodeScanner { barcode ->
                val added = GroceryRepository.addToGroceriesByBarcode(barcode)
                val msg = when {
                    added -> getString(R.string.barcode_added)
                    GroceryRepository.findByBarcode(barcode) != null -> getString(R.string.barcode_already_in_list)
                    else -> getString(R.string.barcode_not_linked)
                }
                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                if (added) adapter.notifyItemInserted(GroceryRepository.groceryList.size - 1)
            }
        }

        setupSwipeHandler(recyclerView)
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
                val alreadyInInventory = GroceryRepository.inventoryList.any { it.nameRes == item.nameRes }
                return if (alreadyInInventory) ItemTouchHelper.LEFT else super.getSwipeDirs(recyclerView, viewHolder)
            }

            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.bindingAdapterPosition
                val item = GroceryRepository.groceryList[position]
                if (direction == ItemTouchHelper.LEFT) {
                    GroceryRepository.removeFromGroceries(item, position)
                    adapter.notifyItemRemoved(position)
                } else {
                    GroceryRepository.moveToInventory(item, position)
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
