package com.dannyjung.rangebar

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.dannyjung.rangebar.base.BaseTrack
import com.dannyjung.rangebar.utils.dp

class ForegroundTrack(context: Context) : BaseTrack() {

    private val paint by lazy {
        Paint().apply {
            isAntiAlias = true
            color = Color.BLUE
            style = Paint.Style.FILL
        }
    }

    override val height: Int = context.dp(10)

    override fun onDraw(canvas: Canvas) {
        canvas.drawRoundRect(
            rectF,
            height.toFloat(),
            height.toFloat(),
            paint
        )
    }
}
