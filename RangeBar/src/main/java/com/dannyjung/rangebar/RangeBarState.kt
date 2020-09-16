package com.dannyjung.rangebar

data class RangeBarState(
    val value: Value = Value(),
    val touchAction: TouchAction = TouchAction.Idle
) {

    data class Value(
        val startValue: Int = 0,
        val endValue: Int = 10,
        val interval: Int = 1,
        val selectedStartValue: Int = startValue,
        val selectedEndValue: Int = endValue
    )

    sealed class TouchAction {

        data class Down(
            val thumb: Thumb,
            val x: Float
        ) : TouchAction()

        data class Move(
            val thumb: Thumb,
            val x: Float
        ) : TouchAction()

        object Idle : TouchAction()

        enum class Thumb {
            START,
            END
        }
    }
}
