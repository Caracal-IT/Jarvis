package com.github.caracal.jarvis.shopping.replenish

import android.view.GestureDetector
import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView

/**
 * OnItemTouchListener for detecting double-tap gestures on Replenish List items.
 *
 * When a user double-taps an item, the [onDoubleTap] callback is invoked with the
 * tapped position.
 *
 * @param recyclerView The RecyclerView to attach the listener to.
 * @param onDoubleTap Callback invoked when an item is double-tapped with its adapter position.
 */
class DoubleTapItemTouchListener(
    recyclerView: RecyclerView,
    private val onDoubleTap: (position: Int) -> Unit
) : RecyclerView.OnItemTouchListener {

    private val gestureDetector = GestureDetector(
        recyclerView.context,
        object : GestureDetector.SimpleOnGestureListener() {
            override fun onDoubleTap(e: MotionEvent): Boolean {
                val childView = recyclerView.findChildViewUnder(e.x, e.y)
                if (childView != null) {
                    val position = recyclerView.getChildAdapterPosition(childView)
                    if (position != RecyclerView.NO_POSITION) {
                        onDoubleTap(position)
                        return true
                    }
                }
                return false
            }

            override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                // Let normal click handling proceed
                return false
            }
        }
    )

    override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
        gestureDetector.onTouchEvent(e)
        return false  // Don't intercept, let RecyclerView handle clicks normally
    }

    override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {
        // Not used
    }

    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
        // Not used
    }
}

