package com.github.caracal.jarvis.groceries

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.caracal.jarvis.R

class GroceriesListFragment : Fragment() {

    private val groceryItems: List<GroceryItem> = listOf(
        GroceryItem(R.string.item_apple,  R.drawable.ic_apple,  R.drawable.bg_icon_red),
        GroceryItem(R.string.item_bread,  R.drawable.ic_bread,  R.drawable.bg_icon_orange),
        GroceryItem(R.string.item_milk,   R.drawable.ic_milk,   R.drawable.bg_icon_blue),
        GroceryItem(R.string.item_eggs,   R.drawable.ic_egg,    R.drawable.bg_icon_yellow),
        GroceryItem(R.string.item_cheese, R.drawable.ic_cheese, R.drawable.bg_icon_navy),
        GroceryItem(R.string.item_water,  R.drawable.ic_water,  R.drawable.bg_icon_blue),
        GroceryItem(R.string.item_butter, R.drawable.ic_milk,   R.drawable.bg_icon_orange),
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
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = GroceryAdapter(groceryItems)
    }
}
