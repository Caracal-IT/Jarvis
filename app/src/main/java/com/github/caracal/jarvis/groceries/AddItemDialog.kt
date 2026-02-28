package com.github.caracal.jarvis.groceries

import android.app.Dialog
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.github.caracal.jarvis.R

/**
 * Dialog for manually entering a product name when barcode lookup fails.
 * Auto-generates an appropriate icon based on the entered name.
 */
class AddItemDialog : DialogFragment() {

    var barcode: String? = null
    var prefilledName: String? = null
    var onItemAdded: (() -> Unit)? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = layoutInflater.inflate(R.layout.dialog_add_item, null)
        val etName = view.findViewById<EditText>(R.id.etItemName)

        // Pre-fill with suggested name if provided
        prefilledName?.let { etName.setText(it) }

        return AlertDialog.Builder(requireContext())
            .setTitle(R.string.add_item_title)
            .setView(view)
            .setPositiveButton(R.string.add) { _, _ ->
                val name = etName.text.toString().trim()
                if (name.isBlank()) {
                    Toast.makeText(requireContext(), R.string.name_required, Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                // Check if this name already exists in base items
                val existing = GroceryRepository.findByName(name) { resId -> getString(resId) }
                if (existing != null) {
                    // Merge: link the scanned barcode to the existing item, add to grocery list
                    barcode?.let { GroceryRepository.linkBarcode(it, existing) }
                    GroceryRepository.addToGroceriesByBarcode(
                        barcode ?: ""
                    ).also { added ->
                        if (!added) {
                            // barcode was blank or item already in list — try by reference
                            if (GroceryRepository.groceryList.none { it === existing }) {
                                GroceryRepository.groceryList.add(existing)
                            }
                        }
                    }
                } else {
                    // Genuinely new item — create with auto-generated icon
                    val iconSet = IconGenerator.resolve(name, "")
                    val item = GroceryItem.Dynamic(
                        name = name,
                        iconRes = iconSet.iconRes,
                        iconBgRes = iconSet.bgRes,
                        barcodes = if (barcode != null) mutableSetOf(barcode!!) else mutableSetOf()
                    )
                    GroceryRepository.addDynamicItem(item, addToGroceries = true)
                }

                onItemAdded?.invoke()
            }
            .setNegativeButton(R.string.cancel, null)
            .create()
    }
}
