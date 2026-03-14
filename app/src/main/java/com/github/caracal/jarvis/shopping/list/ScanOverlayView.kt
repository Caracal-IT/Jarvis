package com.github.caracal.jarvis.shopping.list

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View

/**
 * Full-screen overlay that dims the screen and cuts out a centered rounded rectangle.
 * The rectangle size is responsive (percentage of the smaller screen dimension).
 *
 * This version uses a darker scrim to create a more prominent "blur-like" effect on the
 * camera preview.
 */
class ScanOverlayView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : View(context, attrs, defStyle) {

    private val scrimPaint = Paint().apply { isAntiAlias = true }
    private val clearPaint = Paint().apply { xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR); isAntiAlias = true }
    private val strokePaint = Paint().apply { style = Paint.Style.STROKE; strokeWidth = 4f; isAntiAlias = true }
    private val frameRect = RectF()
    private val cornerRadiusPx: Float

    // responsive fraction of the smaller dimension
    private var frameFraction = 0.55f
    private var frameSizePx = 0f

    // pulse animation state
    private var pulseAnimator: ValueAnimator? = null

    init {
        cornerRadiusPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8f, resources.displayMetrics)
        // Initialize paints with color resources to avoid hardcoded values
        scrimPaint.color = context.getColor(com.github.caracal.jarvis.R.color.overlay_scrim_strong)
        strokePaint.color = context.getColor(com.github.caracal.jarvis.R.color.overlay_frame)
        setLayerType(LAYER_TYPE_SOFTWARE, null) // required for PorterDuff CLEAR
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        // compute frame size as fraction of smaller dimension
        val min = w.coerceAtMost(h)
        frameSizePx = frameFraction * min
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // draw full scrim
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), scrimPaint)

        // compute centered frame rect
        val left = (width - frameSizePx) / 2f
        val top = (height - frameSizePx) / 2f
        frameRect.set(left, top, left + frameSizePx, top + frameSizePx)

        // clear rounded rect
        canvas.drawRoundRect(frameRect, cornerRadiusPx, cornerRadiusPx, clearPaint)

        // draw frame border
        canvas.drawRoundRect(frameRect, cornerRadiusPx, cornerRadiusPx, strokePaint)
    }

    /** Returns the current scan frame as a RectF in view coordinates. */
    @Suppress("unused")
    fun getFrameRect(): RectF = RectF(frameRect)

    /**
     * Pulse the frame border briefly to give visual feedback (used when barcode center is inside).
     */
    @Suppress("unused")
    fun pulse() {
        pulseAnimator?.cancel()
        pulseAnimator = ValueAnimator.ofFloat(0f, 1f, 0f).apply {
            duration = 400
            addUpdateListener { v ->
                val t = v.animatedValue as Float
                // interpolate stroke alpha (0..255)
                strokePaint.alpha = (255 * t).toInt()
                invalidate()
            }
            start()
        }
    }
}
