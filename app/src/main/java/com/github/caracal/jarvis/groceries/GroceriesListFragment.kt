package com.github.caracal.jarvis.groceries

import android.graphics.Canvas
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.core.graphics.toColorInt
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.caracal.jarvis.R

class GroceriesListFragment : Fragment() {

    private val groceryItems: MutableList<GroceryItem> = mutableListOf(
        GroceryItem(R.string.item_apple,   R.drawable.ic_apple,   R.drawable.bg_icon_red),
        GroceryItem(R.string.item_bread,   R.drawable.ic_bread,   R.drawable.bg_icon_orange),
        GroceryItem(R.string.item_milk,    R.drawable.ic_milk,    R.drawable.bg_icon_blue),
        GroceryItem(R.string.item_eggs,    R.drawable.ic_egg,     R.drawable.bg_icon_yellow),
        GroceryItem(R.string.item_cheese,  R.drawable.ic_cheese,  R.drawable.bg_icon_navy),
        GroceryItem(R.string.item_water,   R.drawable.ic_water,   R.drawable.bg_icon_blue),
        GroceryItem(R.string.item_butter,  R.drawable.ic_milk,    R.drawable.bg_icon_orange),
        GroceryItem(R.string.item_yogurt,  R.drawable.ic_milk,    R.drawable.bg_icon_white),
        GroceryItem(R.string.item_peanuts, R.drawable.ic_peanuts, R.drawable.bg_icon_dark_green)
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_groceries_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.rvGroceries)
        val adapter = GroceryAdapter(groceryItems)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        val swipeBackground = "#7A0019".toColorInt().toDrawable()
        val deleteIcon = ResourcesCompat.getDrawable(resources, R.drawable.ic_delete, requireContext().theme)!!
        val iconMargin = resources.getDimensionPixelSize(R.dimen.swipe_icon_margin)

        val swipeHandler = object : ItemTouchHelper.SimpleCallback(
            0, ItemTouchHelper.LEFT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                adapter.removeItem(viewHolder.bindingAdapterPosition)
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float, dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                val itemView = viewHolder.itemView
                val iconTop = itemView.top + (itemView.height - deleteIcon.intrinsicHeight) / 2
                val iconBottom = iconTop + deleteIcon.intrinsicHeight
                val iconLeft = itemView.right - iconMargin - deleteIcon.intrinsicWidth
                val iconRight = itemView.right - iconMargin

                // Draw red background
                swipeBackground.setBounds(
                    itemView.right + dX.toInt(),
                    itemView.top, itemView.right, itemView.bottom
                )
                swipeBackground.draw(c)

                // Draw delete icon only when enough space is revealed
                if (dX < -iconMargin * 2) {
                    deleteIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                    deleteIcon.draw(c)
                }

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
        }

        ItemTouchHelper(swipeHandler).attachToRecyclerView(recyclerView)
    }
}
