package com.github.caracal.jarvis.shopping.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.github.caracal.jarvis.R
import com.github.caracal.jarvis.databinding.ShoppingListAddItemBinding
import com.github.caracal.jarvis.shopping.data.ShoppingCategory
import com.github.caracal.jarvis.shopping.ui.ShoppingViewModel
import com.github.caracal.jarvis.shopping.ui.ShoppingViewModelFactory

/**
 * Fragment for adding a new item to the Shopping List.
 */
class AddItemFragment : Fragment() {

    private var _binding: ShoppingListAddItemBinding? = null
    private val binding get() = _binding!!

    private val shoppingViewModel: ShoppingViewModel by activityViewModels {
        ShoppingViewModelFactory(requireContext())
    }

    private var categories: List<ShoppingCategory> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ShoppingListAddItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        shoppingViewModel.categories.observe(viewLifecycleOwner) { categoryList ->
            categories = categoryList
            val categoryNames = categories.map { it.name }
            val spinnerAdapter = ArrayAdapter(
                requireContext(),
                R.layout.spinner_item,
                categoryNames
            ).also { it.setDropDownViewResource(R.layout.spinner_dropdown_item) }
            binding.spinnerCategory.adapter = spinnerAdapter
        }

        shoppingViewModel.addItemError.observe(viewLifecycleOwner) { error ->
            binding.tilItemName.error = error
        }

        binding.btnBack.setOnClickListener { parentFragmentManager.popBackStack() }
        binding.btnSave.setOnClickListener {
            val name = binding.etItemName.text?.toString() ?: ""
            val categoryIndex = binding.spinnerCategory.selectedItemPosition
            val isBaseline = binding.swAddToReplenishList.isChecked
            if (categoryIndex >= 0 && categoryIndex < categories.size) {
                val categoryId = categories[categoryIndex].id
                val added = shoppingViewModel.addShoppingItem(name, categoryId, isBaseline)
                
                if (added) {
                    parentFragmentManager.popBackStack()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        shoppingViewModel.clearAddItemError()
        _binding = null
    }
}
