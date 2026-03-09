package com.github.caracal.jarvis.shopping.list

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.github.caracal.jarvis.R
import com.github.caracal.jarvis.databinding.DialogRenameItemBinding
import com.github.caracal.jarvis.shopping.ShoppingFragment

/**
 * Dialog fragment for renaming an existing Shopping List item.
 *
 * Pre-populates the text field with the item's current name and shows inline validation
 * errors via [com.google.android.material.textfield.TextInputLayout].
 * The ViewModel is shared via the grandparent [ShoppingFragment].
 */
class RenameItemDialogFragment : DialogFragment() {

    private var _binding: DialogRenameItemBinding? = null
    private val binding get() = _binding!!

    private val shoppingViewModel by lazy {
        (requireParentFragment().requireParentFragment() as ShoppingFragment).viewModel
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogRenameItemBinding.inflate(layoutInflater)

        val itemId = requireArguments().getString(ARG_ITEM_ID)!!
        val currentName = requireArguments().getString(ARG_CURRENT_NAME)!!
        binding.etNewName.setText(currentName)

        shoppingViewModel.renameItemError.observe(this) { error ->
            binding.tilNewName.error = error
        }

        val dialog = AlertDialog.Builder(requireContext(), R.style.Theme_Jarvis_Dialog)
            .setTitle(R.string.dialog_title_rename_item)
            .setView(binding.root)
            .setPositiveButton(R.string.action_rename, null)
            .setNegativeButton(R.string.action_cancel, null)
            .create()

        dialog.window?.setBackgroundDrawableResource(R.drawable.bg_dialog)

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val newName = binding.etNewName.text?.toString() ?: ""
                val renamed = shoppingViewModel.renameShoppingItem(itemId, newName)
                if (renamed) dismiss()
            }
        }
        return dialog
    }

    override fun onDestroyView() {
        super.onDestroyView()
        shoppingViewModel.clearRenameItemError()
        _binding = null
    }

    companion object {
        private const val ARG_ITEM_ID = "item_id"
        private const val ARG_CURRENT_NAME = "current_name"

        /**
         * Creates a new instance with the required arguments.
         *
         * @param itemId The ID of the item to rename.
         * @param currentName The item's current display name.
         */
        fun newInstance(itemId: String, currentName: String): RenameItemDialogFragment =
            RenameItemDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_ITEM_ID, itemId)
                    putString(ARG_CURRENT_NAME, currentName)
                }
            }
    }
}
