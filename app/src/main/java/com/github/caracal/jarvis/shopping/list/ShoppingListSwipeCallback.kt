package com.github.caracal.jarvis.shopping.list

import android.graphics.Canvas
import android.view.animation.DecelerateInterpolator
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.github.caracal.jarvis.R
import com.github.caracal.jarvis.shopping.ui.ShoppingDisplayItem

/**
 * ItemTouchHelper callback for swipe gestures on Shopping List items.
 *
 * Behavior:
 * - Partial swipe reveals actions and stays open.
 * - Swiping in the opposite direction closes the open actions.
 * - Full swipe LEFT deletes the item.
 * - Full swipe RIGHT triggers the primary right action (currently edit/rename).
 */
class ShoppingListSwipeCallback(
    private val adapter: ShoppingListAdapter,
    private val onFullSwipeEdit: (ShoppingDisplayItem.Item) -> Unit,
    private val onFullSwipeDelete: (ShoppingDisplayItem.Item) -> Unit
) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

    private val editRevealWidthDp = 80f
    private val deleteRevealWidthDp = 80f
    private val minOpenTriggerDp = 8f

    private var activeViewHolderKey: Int? = null
    private var swipeStartTranslationX = 0f

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        return if (viewHolder is ShoppingListAdapter.ItemViewHolder) {
            makeMovementFlags(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT)
        } else {
            0
        }
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean = false

    override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
        // Full swipe is intentional; partial swipes should stay revealed instead.
        return 0.65f
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.bindingAdapterPosition
        if (position == RecyclerView.NO_POSITION) return

        val row = adapter.currentList.getOrNull(position)
        if (row !is ShoppingDisplayItem.Item) {
            adapter.notifyItemChanged(position)
            return
        }

        // Trigger action callbacks on full swipe
        when (direction) {
            ItemTouchHelper.LEFT -> {
                viewHolder.itemView.post {
                    onFullSwipeDelete(row)
                }
            }
            ItemTouchHelper.RIGHT -> {
                viewHolder.itemView.post {
                    onFullSwipeEdit(row)
                }
            }
        }
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        if (viewHolder !is ShoppingListAdapter.ItemViewHolder) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            return
        }

        if (!isCurrentlyActive || actionState != ItemTouchHelper.ACTION_STATE_SWIPE) {
            return
        }

        val density = viewHolder.itemView.resources.displayMetrics.density
        val editRevealWidthPx = editRevealWidthDp * density
        val deleteRevealWidthPx = deleteRevealWidthDp * density

        val foreground = viewHolder.getForeground()
        val holderKey = System.identityHashCode(viewHolder)

        if (activeViewHolderKey != holderKey) {
            activeViewHolderKey = holderKey
            swipeStartTranslationX = foreground.translationX
        }

        val raw = swipeStartTranslationX + dX
        val clamped = raw.coerceIn(
            -viewHolder.itemView.width.toFloat(),
            viewHolder.itemView.width.toFloat()
        )
        foreground.translationX = clamped

        // Show visible part immediately, with progressive alpha based on reveal amount.
        if (clamped > 0f) {
            val progress = (clamped / editRevealWidthPx).coerceIn(0f, 1f)
            viewHolder.setLeftRevealFraction(progress)
            viewHolder.setRightRevealFraction(0f)
            // Set background to green for edit action
            viewHolder.setBackgroundColor(R.color.swipe_action_green_transparent)
        } else if (clamped < 0f) {
            val progress = ((-clamped) / deleteRevealWidthPx).coerceIn(0f, 1f)
            viewHolder.setLeftRevealFraction(0f)
            viewHolder.setRightRevealFraction(progress)
            // Set background to red for delete action
            viewHolder.setBackgroundColor(R.color.iron_man_red)
        } else {
            viewHolder.setLeftRevealFraction(0f)
            viewHolder.setRightRevealFraction(0f)
        }
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        if (viewHolder !is ShoppingListAdapter.ItemViewHolder) {
            super.clearView(recyclerView, viewHolder)
            return
        }

        val density = viewHolder.itemView.resources.displayMetrics.density
        val editRevealWidthPx = editRevealWidthDp * density
        val deleteRevealWidthPx = deleteRevealWidthDp * density
        val minOpenTriggerPx = minOpenTriggerDp * density

        val foreground = viewHolder.getForeground()
        val current = foreground.translationX

        val target = when {
            current >= minOpenTriggerPx -> editRevealWidthPx
            current <= -minOpenTriggerPx -> -deleteRevealWidthPx
            else -> 0f
        }

        foreground.animate()
            .translationX(target)
            .setDuration(160)
            .setInterpolator(DecelerateInterpolator())
            .withEndAction {
                when {
                    target > 0f -> {
                        viewHolder.setLeftRevealFraction(1f)
                        viewHolder.setRightRevealFraction(0f)
                    }
                    target < 0f -> {
                        viewHolder.setLeftRevealFraction(0f)
                        viewHolder.setRightRevealFraction(1f)
                    }
                    else -> {
                        viewHolder.setLeftRevealFraction(0f)
                        viewHolder.setRightRevealFraction(0f)
                    }
                }
            }
            .start()

        activeViewHolderKey = null
        swipeStartTranslationX = 0f

        super.clearView(recyclerView, viewHolder)
    }
}
