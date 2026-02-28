package com.github.caracal.jarvis.groceries

import android.content.Context
import android.graphics.Typeface
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.core.view.setPadding
import com.github.caracal.jarvis.R

/**
 * Shows a rename dialog pre-filled with the item's current name.
 *
 * On save:
 * - If the new name already exists in inventory → merge barcodes from [item] into
 *   the existing item, then remove [item] from all lists (no duplicate created).
 * - Otherwise → rename [item] in-place across all lists.
 *
 * Static items are promoted to Dynamic on rename.
 * Calls [onRenamed] so the adapter can refresh.
 */
fun showRenameDialog(
    context: Context,
    item: GroceryItem,
    position: Int,
    onRenamed: (position: Int) -> Unit
) {
    val currentName = item.getName { resId -> context.getString(resId) }

    val editText = EditText(context).apply {
        setText(currentName)
        selectAll()
        typeface = Typeface.MONOSPACE
        setTextColor(context.getColor(R.color.iron_man_cyan))
        setHintTextColor(context.getColor(R.color.iron_man_cyan))
    }

    val container = LinearLayout(context).apply {
        orientation = LinearLayout.VERTICAL
        setPadding(64)
        addView(editText)
    }

    val dialog = AlertDialog.Builder(context)
        .setTitle(R.string.rename_item_title)
        .setView(container)
        .setPositiveButton(R.string.save) { _, _ ->
            val newName = editText.text.toString().trim()
            if (newName.isBlank() || newName.equals(currentName, ignoreCase = true)) return@setPositiveButton

            // Check if the new name already exists in inventory
            val existing = GroceryRepository.findByName(newName) { resId -> context.getString(resId) }

            if (existing != null && existing !== item) {
                // Merge: copy all barcodes from the item being renamed into the existing one,
                // then remove the duplicate from every list.
                item.barcodes.forEach { barcode -> GroceryRepository.linkBarcode(barcode, existing) }
                GroceryRepository.removeDuplicate(item)
            } else {
                // Safe to rename — no conflict
                GroceryRepository.renameItem(item, newName)
            }

            onRenamed(position)
        }
        .setNegativeButton(R.string.cancel, null)
        .create()

    dialog.show()

    // Show keyboard automatically
    editText.postDelayed({
        editText.requestFocus()
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
    }, 100)
}
