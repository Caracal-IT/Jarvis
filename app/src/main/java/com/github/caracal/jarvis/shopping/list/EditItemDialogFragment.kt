package com.github.caracal.jarvis.shopping.list

import android.app.Dialog
import android.os.Bundle
import android.view.Window
import androidx.fragment.app.DialogFragment
import com.github.caracal.jarvis.databinding.FragmentEditItemBinding
import com.github.caracal.jarvis.shopping.ShoppingFragment

/**
 * Full-screen dialog fragment for editing a Shopping List item name.
 *
 * Provides a text field to edit the item name with validation feedback.
 * A back button allows dismissing the dialog and returning to the Shopping List.
 * The ViewModel is shared via the grandparent [ShoppingFragment].
 */
class EditItemDialogFragment : DialogFragment() {

    private var _binding: FragmentEditItemBinding? = null
    private val binding get() = _binding!!

    private val shoppingViewModel by lazy {
        (requireParentFragment().requireParentFragment() as ShoppingFragment).viewModel
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = FragmentEditItemBinding.inflate(layoutInflater)

        val dialog = Dialog(requireContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(binding.root)

        val itemId = arguments?.getString(ARG_ITEM_ID) ?: return dialog
        val itemName = arguments?.getString(ARG_ITEM_NAME) ?: return dialog

        // Pre-fill with current name
        binding.etItemName.setText(itemName)

        // Back button - dismiss dialog
        binding.btnBack.setOnClickListener {
            dismiss()
        }

        // Save button
        binding.btnSave.setOnClickListener {
            val newName = binding.etItemName.text?.toString() ?: ""
            val renamed = shoppingViewModel.renameShoppingItem(itemId, newName)
            if (renamed) {
                dismiss()
            }
        }

        // Observe validation errors
        shoppingViewModel.renameItemError.observe(this) { error ->
            binding.tilItemName.error = error
        }

        return dialog
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_ITEM_ID = "item_id"
        private const val ARG_ITEM_NAME = "item_name"

        fun newInstance(itemId: String, itemName: String): EditItemDialogFragment =
            EditItemDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_ITEM_ID, itemId)
                    putString(ARG_ITEM_NAME, itemName)
                }
            }
    }
}

