package com.github.caracal.jarvis.shopping.list

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.Window
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import com.github.caracal.jarvis.R
import com.github.caracal.jarvis.databinding.ShoppingListAddItemBinding
import com.github.caracal.jarvis.shopping.ShoppingFragment
import com.github.caracal.jarvis.shopping.data.ShoppingCategory

/**
 * Dialog fragment for adding a new item to the Shopping List.
 *
 * Provides a text field for the item name and a spinner for category selection.
 * Validation errors are shown inline via [com.google.android.material.textfield.TextInputLayout].
 * The ViewModel is shared via the grandparent [ShoppingFragment].
 */
class AddItemDialogFragment : DialogFragment() {

    private var _binding: ShoppingListAddItemBinding? = null
    private val binding get() = _binding!!

    private val shoppingViewModel by lazy {
        (requireParentFragment().requireParentFragment() as ShoppingFragment).viewModel
    }

    private var categories: List<ShoppingCategory> = emptyList()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = ShoppingListAddItemBinding.inflate(layoutInflater)

        val dialog = Dialog(requireContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(binding.root)

        categories = shoppingViewModel.categories.value ?: emptyList()
        val categoryNames = categories.map { it.name }
        val spinnerAdapter = ArrayAdapter(
            requireContext(),
            R.layout.spinner_item,
            categoryNames
        ).also { it.setDropDownViewResource(R.layout.spinner_dropdown_item) }
        binding.spinnerCategory.adapter = spinnerAdapter

        shoppingViewModel.addItemError.observe(this) { error ->
            binding.tilItemName.error = error
        }

        binding.btnBack.setOnClickListener { dismiss() }
        binding.btnSave.setOnClickListener {
            val name = binding.etItemName.text?.toString() ?: ""
            val categoryIndex = binding.spinnerCategory.selectedItemPosition
            if (categoryIndex >= 0 && categoryIndex < categories.size) {
                val categoryId = categories[categoryIndex].id
                val added = shoppingViewModel.addShoppingItem(name, categoryId)
                if (added) dismiss()
            }
        }

        return dialog
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        // Reset swipe states when returning to the shopping list.
        (parentFragment as? ShoppingListFragment)?.resetAllItemsSwipeState()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        shoppingViewModel.clearAddItemError()
        _binding = null
    }
}
