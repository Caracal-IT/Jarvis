package com.github.caracal.jarvis.shopping.list

import android.app.Dialog
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.github.caracal.jarvis.R
import com.github.caracal.jarvis.databinding.DialogAddItemBinding
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

    private var _binding: DialogAddItemBinding? = null
    private val binding get() = _binding!!

    private val shoppingViewModel by lazy {
        (requireParentFragment().requireParentFragment() as ShoppingFragment).viewModel
    }

    private var categories: List<ShoppingCategory> = emptyList()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogAddItemBinding.inflate(layoutInflater)

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

        val dialog = AlertDialog.Builder(requireContext(), R.style.Theme_Jarvis_Dialog)
            .setTitle(R.string.dialog_title_add_item)
            .setView(binding.root)
            .setPositiveButton(R.string.action_add, null)
            .setNegativeButton(R.string.action_cancel, null)
            .create()

        dialog.window?.setBackgroundDrawableResource(R.drawable.bg_dialog)

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val name = binding.etItemName.text?.toString() ?: ""
                val categoryIndex = binding.spinnerCategory.selectedItemPosition
                if (categoryIndex >= 0 && categoryIndex < categories.size) {
                    val categoryId = categories[categoryIndex].id
                    val added = shoppingViewModel.addShoppingItem(name, categoryId)
                    if (added) dismiss()
                }
            }
        }
        return dialog
    }

    override fun onDestroyView() {
        super.onDestroyView()
        shoppingViewModel.clearAddItemError()
        _binding = null
    }
}
