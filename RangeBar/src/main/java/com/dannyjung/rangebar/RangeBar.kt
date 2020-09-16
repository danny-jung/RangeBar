package com.dannyjung.rangebar

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.dannyjung.rangebar.base.BaseThumb
import com.dannyjung.rangebar.base.BaseTickMark
import com.dannyjung.rangebar.base.BaseTrack
import kotlin.math.abs
import kotlin.math.max

class RangeBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    var backgroundTrack: BaseTrack = BackgroundTrack(context)
        set(value) {
            if (value == field || state.touchAction !is RangeBarState.TouchAction.Idle) return
            field = value
            invalidate()
        }

    var foregroundTrack: BaseTrack = ForegroundTrack(context)
        set(value) {
            if (value == field || state.touchAction !is RangeBarState.TouchAction.Idle) return
            field = value
            invalidate()
        }

    var tickMark: BaseTickMark = TickMark(context)
        set(value) {
            if (value == field || state.touchAction !is RangeBarState.TouchAction.Idle) return
            field = value
            invalidate()
        }

    var startThumb: BaseThumb = Thumb(context)
        set(value) {
            if (value == field || state.touchAction !is RangeBarState.TouchAction.Idle) return
            field = value
            requestLayout()
        }

    var endThumb: BaseThumb = Thumb(context)
        set(value) {
            if (value == field || state.touchAction !is RangeBarState.TouchAction.Idle) return
            field = value
            requestLayout()
        }

    var onMoved: ((start: Int, end: Int) -> Unit)? = null
    var onSelected: ((start: Int, end: Int) -> Unit)? = null

    var state: RangeBarState = RangeBarState()
        private set(value) {
            if (value == field) return
            field = value
            invalidate()
        }

    private var tickValues: Map<Int, Float>? = null

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val measureHeightMode = MeasureSpec.getMode(heightMeasureSpec)
        val measureHeight = MeasureSpec.getSize(heightMeasureSpec)

        val height = when (measureHeightMode) {
            MeasureSpec.AT_MOST -> {
                max(startThumb.size, endThumb.size)
            }
            else -> {
                measureHeight
            }
        }

        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), height)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        resetTickValues(state.value)
        resetTickMarkRectF(state.value)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        setBackgroundTrackRectF()
        setForegroundTrackRectF()
        setTickMarkRectF()
        setStartThumbRectF()
        setEndThumbRectF()

        backgroundTrack.onDraw(canvas)
        foregroundTrack.onDraw(canvas)
        tickMark.onDraw(canvas)
        startThumb.onDraw(canvas)
        endThumb.onDraw(canvas)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean =
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                onTouchDown(event)
                true
            }
            MotionEvent.ACTION_MOVE -> {
                onTouchMove(event)
                true
            }
            MotionEvent.ACTION_UP -> {
                onTouchUp()
                true
            }
            else -> {
                super.onTouchEvent(event)
            }
        }

    fun changeRangeConfig(value: RangeBarState.Value) {
        val oldState = state

        state = state.copy(value = value)

        if (oldState.value.startValue != value.startValue ||
            oldState.value.endValue != value.endValue ||
            oldState.value.interval != value.interval
        ) {
            resetTickValues(value)
        }

        if (oldState.value.startValue != value.startValue ||
            oldState.value.endValue != value.endValue ||
            oldState.value.interval != value.interval ||
            tickMark.size > 0
        ) {
            resetTickMarkRectF(value)
        }
    }

    private fun resetTickValues(value: RangeBarState.Value) {
        tickValues = (value.startValue..value.endValue)
            .filter { it % value.interval == 0 }
            .mapIndexed { index, it ->
                it to index * getTickIntervalWidth() + getFirstTickX()
            }
            .toMap()
    }

    private fun resetTickMarkRectF(value: RangeBarState.Value) {
        tickMark.rectFs.apply {
            clear()
            addAll(
                (0..(value.endValue - value.startValue))
                    .filter { it % value.interval == 0 }
                    .map { RectF() }
            )
        }
    }

    private fun setBackgroundTrackRectF() {
        backgroundTrack.rectF.set(
            getFirstTickX(),
            (height.toFloat() - backgroundTrack.height) / 2,
            getLastTickX(),
            (height.toFloat() + backgroundTrack.height) / 2
        )
    }

    private fun setForegroundTrackRectF() {
        foregroundTrack.rectF.set(
            getCurrentStartThumbX(),
            (height.toFloat() - foregroundTrack.height) / 2,
            getCurrentEndThumbX(),
            (height.toFloat() + foregroundTrack.height) / 2
        )
    }

    private fun setTickMarkRectF() {
        tickMark.rectFs.forEachIndexed { index, rectF ->
            val left = getFirstTickX() + (getTickIntervalWidth() * index) - (tickMark.size / 2)
            val top = (height.toFloat() - tickMark.size) / 2
            rectF.set(
                left,
                top,
                left + tickMark.size,
                top + tickMark.size
            )
        }
    }

    private fun setStartThumbRectF() {
        val touchAction = state.touchAction

        val left = when {
            touchAction is RangeBarState.TouchAction.Down &&
                    touchAction.thumb == RangeBarState.TouchAction.Thumb.START -> {
                touchAction.x
            }
            touchAction is RangeBarState.TouchAction.Move &&
                    touchAction.thumb == RangeBarState.TouchAction.Thumb.START -> {
                touchAction.x
            }
            else -> {
                getTickX(state.value.selectedStartValue, getFirstTickX())
            }
        } - (startThumb.size / 2)

        val top = (height.toFloat() - startThumb.size) / 2

        startThumb.rectF.set(
            left,
            top,
            left + startThumb.size,
            top + startThumb.size
        )
    }

    private fun setEndThumbRectF() {
        val touchAction = state.touchAction

        val left = when {
            touchAction is RangeBarState.TouchAction.Down &&
                    touchAction.thumb == RangeBarState.TouchAction.Thumb.END -> {
                touchAction.x
            }
            touchAction is RangeBarState.TouchAction.Move &&
                    touchAction.thumb == RangeBarState.TouchAction.Thumb.END -> {
                touchAction.x
            }
            else -> {
                getTickX(state.value.selectedEndValue, getLastTickX())
            }
        } - (endThumb.size / 2)

        val top = (height.toFloat() - endThumb.size) / 2

        endThumb.rectF.set(
            left,
            top,
            left + endThumb.size,
            top + endThumb.size
        )
    }

    private fun onTouchDown(event: MotionEvent) {
        val x = event.x

        val thumb = when {
            x <= getCurrentStartThumbX() -> {
                RangeBarState.TouchAction.Thumb.START
            }
            x >= getCurrentEndThumbX() -> {
                RangeBarState.TouchAction.Thumb.END
            }
            x - getCurrentStartThumbX() <= getCurrentEndThumbX() - x -> {
                RangeBarState.TouchAction.Thumb.START
            }
            else -> {
                RangeBarState.TouchAction.Thumb.END
            }
        }

        val calculatedX = when (thumb) {
            RangeBarState.TouchAction.Thumb.START -> {
                x.coerceAtMost(getMovableMaxStartThumbX()).coerceAtLeast(getFirstTickX())
            }
            RangeBarState.TouchAction.Thumb.END -> {
                x.coerceAtLeast(getMovableMinEndThumbX()).coerceAtMost(getLastTickX())
            }
        }

        val calculatedClosestValue = findClosestValue(calculatedX) ?: return

        state = state.copy(
            value = state.value.copy(
                selectedStartValue = when (thumb) {
                    RangeBarState.TouchAction.Thumb.START -> {
                        calculatedClosestValue
                    }
                    RangeBarState.TouchAction.Thumb.END -> {
                        state.value.selectedStartValue
                    }
                },
                selectedEndValue = when (thumb) {
                    RangeBarState.TouchAction.Thumb.START -> {
                        state.value.selectedEndValue
                    }
                    RangeBarState.TouchAction.Thumb.END -> {
                        calculatedClosestValue
                    }
                }
            ),
            touchAction = RangeBarState.TouchAction.Down(
                thumb = thumb,
                x = calculatedX
            )
        )

        onMoved?.invoke(state.value.selectedStartValue, state.value.selectedEndValue)
    }

    private fun onTouchMove(event: MotionEvent) {
        val x = event.x

        val thumb = when (val touchAction = state.touchAction) {
            is RangeBarState.TouchAction.Down -> {
                touchAction.thumb
            }
            is RangeBarState.TouchAction.Move -> {
                touchAction.thumb
            }
            else -> null
        } ?: return

        val calculatedX = when (thumb) {
            RangeBarState.TouchAction.Thumb.START -> {
                x.coerceAtMost(getMovableMaxStartThumbX()).coerceAtLeast(getFirstTickX())
            }
            RangeBarState.TouchAction.Thumb.END -> {
                x.coerceAtLeast(getMovableMinEndThumbX()).coerceAtMost(getLastTickX())
            }
        }

        val calculatedClosestValue = findClosestValue(calculatedX) ?: return

        state = state.copy(
            value = state.value.copy(
                selectedStartValue = when (thumb) {
                    RangeBarState.TouchAction.Thumb.START -> {
                        calculatedClosestValue
                    }
                    RangeBarState.TouchAction.Thumb.END -> {
                        state.value.selectedStartValue
                    }
                },
                selectedEndValue = when (thumb) {
                    RangeBarState.TouchAction.Thumb.START -> {
                        state.value.selectedEndValue
                    }
                    RangeBarState.TouchAction.Thumb.END -> {
                        calculatedClosestValue
                    }
                }
            ),
            touchAction = RangeBarState.TouchAction.Move(
                thumb = thumb,
                x = calculatedX
            )
        )

        onMoved?.invoke(state.value.selectedStartValue, state.value.selectedEndValue)
    }

    private fun onTouchUp() {
        val (thumb, x) = when (val touchAction = state.touchAction) {
            is RangeBarState.TouchAction.Down -> {
                Pair(touchAction.thumb, touchAction.x)
            }
            is RangeBarState.TouchAction.Move -> {
                Pair(touchAction.thumb, touchAction.x)
            }
            else -> Pair(null, null)
        }

        if (thumb == null || x == null) return

        val closestValue = findClosestValue(x) ?: return

        state = state.copy(
            value = state.value.copy(
                selectedStartValue = when (thumb) {
                    RangeBarState.TouchAction.Thumb.START -> {
                        closestValue
                    }
                    RangeBarState.TouchAction.Thumb.END -> {
                        state.value.selectedStartValue
                    }
                },
                selectedEndValue = when (thumb) {
                    RangeBarState.TouchAction.Thumb.START -> {
                        state.value.selectedEndValue
                    }
                    RangeBarState.TouchAction.Thumb.END -> {
                        closestValue
                    }
                }
            ),
            touchAction = RangeBarState.TouchAction.Idle
        )

        onSelected?.invoke(state.value.selectedStartValue, state.value.selectedEndValue)
    }

    private fun getCurrentStartThumbX(): Float {
        val touchAction = state.touchAction
        return when {
            touchAction is RangeBarState.TouchAction.Down &&
                    touchAction.thumb == RangeBarState.TouchAction.Thumb.START -> {
                touchAction.x
            }
            touchAction is RangeBarState.TouchAction.Move &&
                    touchAction.thumb == RangeBarState.TouchAction.Thumb.START -> {
                touchAction.x
            }
            else -> {
                getTickX(state.value.selectedStartValue, getFirstTickX())
            }
        }
    }

    private fun getCurrentEndThumbX(): Float {
        val touchAction = state.touchAction
        return when {
            touchAction is RangeBarState.TouchAction.Down &&
                    touchAction.thumb == RangeBarState.TouchAction.Thumb.END -> {
                touchAction.x
            }
            touchAction is RangeBarState.TouchAction.Move &&
                    touchAction.thumb == RangeBarState.TouchAction.Thumb.END -> {
                touchAction.x
            }
            else -> {
                getTickX(state.value.selectedEndValue, getLastTickX())
            }
        }
    }

    private fun findClosestValue(x: Float): Int? =
        tickValues?.minBy { abs(x.minus(it.value)) }?.key

    private fun getMovableMaxStartThumbX(): Float =
        getTickX(state.value.selectedEndValue - state.value.interval)

    private fun getMovableMinEndThumbX(): Float =
        getTickX(state.value.selectedStartValue + state.value.interval)

    private fun getFirstTickX(): Float = (startThumb.size.toFloat() / 2) + paddingStart

    private fun getLastTickX(): Float = width - (endThumb.size.toFloat() / 2) - paddingEnd

    private fun getTickIntervalWidth(): Float =
        (getLastTickX() - getFirstTickX()) /
                ((state.value.endValue - state.value.startValue) / state.value.interval)

    private fun getTickX(value: Int, defaultValue: Float = 0f): Float =
        tickValues?.get(value)?.let {
            if (it.isNaN()) {
                defaultValue
            } else {
                it
            }
        } ?: defaultValue
}
