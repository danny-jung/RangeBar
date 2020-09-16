package com.dannyjung.rangebar.base

import android.graphics.Canvas
import android.graphics.RectF

abstract class BaseTrack {

    abstract val height: Int

    val rectF: RectF = RectF()

    abstract fun onDraw(canvas: Canvas)
}
