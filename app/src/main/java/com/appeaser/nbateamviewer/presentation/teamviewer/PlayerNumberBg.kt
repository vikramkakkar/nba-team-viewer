package com.appeaser.nbateamviewer.presentation.teamviewer

import android.graphics.*
import android.graphics.drawable.Drawable

/**
 * Basic circular drawable.
 */
class PlayerNumberBg(bgColor: Int) : Drawable() {

    private val circle = Circle()

    private val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = bgColor
        style = Paint.Style.FILL
    }

    override fun draw(canvas: Canvas) {
        if (circle.canBeDrawn()) {
            canvas.drawCircle(circle.centerX, circle.centerY, circle.radius, bgPaint)
        }
    }

    override fun onBoundsChange(bounds: Rect) {
        super.onBoundsChange(bounds)

        if (bounds.isEmpty) return

        circle.apply {
            radius = bounds.height() / 2f
            centerX = bounds.exactCenterX()
            centerY = bounds.exactCenterY()
        }

        invalidateSelf()
    }

    override fun setAlpha(alpha: Int) {
        // no op
    }

    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        // no op
    }

    data class Circle(
        var radius: Float = 0F,
        var centerX: Float = 0F,
        var centerY: Float = 0F
    ) {
        fun canBeDrawn() = radius > 0F
    }
}