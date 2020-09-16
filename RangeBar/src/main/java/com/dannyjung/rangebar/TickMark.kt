package com.dannyjung.rangebar

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.dannyjung.rangebar.base.BaseTickMark
import com.dannyjung.rangebar.utils.dp

class TickMark(context: Context) : BaseTickMark() {

    private val circleRadius: Int = context.dp(0)

    private val paint by lazy {
        Paint().apply {
            isAntiAlias = true
            color = Color.BLACK
            style = Paint.Style.FILL
        }
    }

    override val size: Int = circleRadius * 2

    override fun onDraw(canvas: Canvas) {
        rectFs.forEach { rectF ->
            canvas.drawCircle(
                rectF.left + circleRadius,
                rectF.top + circleRadius,
                circleRadius.toFloat(),
                paint
            )
        }
    }
}
