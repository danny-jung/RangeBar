package com.dannyjung.rangebar.sample

import android.content.Context
import android.view.LayoutInflater

internal inline val Context.layoutInflater: LayoutInflater
    get() = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

internal inline fun Context.dp(dp: Int): Int = (dp * resources.displayMetrics.density).toInt()
