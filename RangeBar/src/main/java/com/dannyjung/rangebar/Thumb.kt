package com.dannyjung.rangebar

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.dannyjung.rangebar.base.BaseThumb
import com.dannyjung.rangebar.utils.dp

class Thumb(context: Context) : BaseThumb() {

    private val circleRadius: Int = context.dp(12)

    private val paint by lazy {
        Paint().apply {
            isAntiAlias = true
            color = Color.GREEN
            style = Paint.Style.FILL
        }
    }

    override val size: Int = circleRadius * 2

    override fun onDraw(canvas: Canvas) {
        canvas.drawCircle(
            rectF.left + circleRadius,
            rectF.top + circleRadius,
            circleRadius.toFloat(),
            paint
        )
    }
}
