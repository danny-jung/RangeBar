package com.dannyjung.rangebar.base

import android.graphics.Canvas
import android.graphics.RectF

abstract class BaseTickMark {

    abstract val size: Int

    val rectFs: MutableList<RectF> = mutableListOf()

    abstract fun onDraw(canvas: Canvas)
}
