package com.dannyjung.rangebar.sample

import android.graphics.Rect
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.dannyjung.rangebar.sample.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val viewBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val layoutManager by lazy {
        GridLayoutManager(this, epoxyController.spanCount).apply {
            spanSizeLookup = epoxyController.spanSizeLookup
        }
    }

    private val epoxyController by lazy { createEpoxyController().apply { spanCount = 2 } }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)

        viewBinding.rangeBar.onMoved = { _, _ ->
            invalidate()
        }

        viewBinding.rangeBar.onSelected = { _, _ ->
            invalidate()
        }

        viewBinding.epoxyRecyclerView.apply {
            layoutManager = this@MainActivity.layoutManager
            setController(epoxyController)
        }

        invalidate()
    }

    private fun createEpoxyController() = RangeBarEpoxyController { state ->
        rangeInfo {
            id("start_value")
            title("StartValue")
            value(state.value.startValue)
            padding(Rect(dp(20), 0, dp(10), 0))
            spanSizeOverride { _, _, _, -> 1 }
        }

        rangeInfo {
            id("end_value")
            title("EndValue")
            value(state.value.endValue)
            padding(Rect(dp(10), 0, dp(20), 0))
            spanSizeOverride { _, _, _, -> 1 }
        }

        rangeInfo {
            id("selected_start_value")
            title("SelectedStartValue")
            value(state.value.selectedStartValue)
            padding(Rect(dp(20), dp(10), dp(10), 0))
            spanSizeOverride { _, _, _, -> 1 }
        }

        rangeInfo {
            id("selected_value")
            title("SelectedEndValue")
            value(state.value.selectedEndValue)
            padding(Rect(dp(10), dp(10), dp(20), 0))
            spanSizeOverride { _, _, _, -> 1 }
        }

        valueController {
            id("start_value_controller")
            title("StartValue")
            value(state.value.startValue)
            max(state.value.endValue)
            padding(Rect(dp(20), dp(30), dp(20), 0))
            onSeekBarChanged {
                viewBinding.rangeBar.changeRangeConfig(
                    viewBinding.rangeBar.state.value.copy(
                        startValue = it,
                        selectedStartValue = it,
                        selectedEndValue = viewBinding.rangeBar.state.value.endValue
                    )
                )
                invalidate()
            }
        }

        valueController {
            id("end_value_controller")
            title("EndValue")
            value(state.value.endValue)
            max(100)
            padding(Rect(dp(20), dp(10), dp(20), 0))
            onSeekBarChanged {
                viewBinding.rangeBar.changeRangeConfig(
                    viewBinding.rangeBar.state.value.copy(
                        endValue = it,
                        selectedStartValue = viewBinding.rangeBar.state.value.startValue,
                        selectedEndValue = it
                    )
                )
                invalidate()
            }
        }

        valueController {
            id("interval_controller")
            title("Interval")
            value(state.value.interval)
            max(10)
            padding(Rect(dp(20), dp(10), dp(20), 0))
            onSeekBarChanged {
                viewBinding.rangeBar.changeRangeConfig(
                    viewBinding.rangeBar.state.value.copy(
                        interval = it,
                        selectedStartValue = viewBinding.rangeBar.state.value.startValue,
                        selectedEndValue = viewBinding.rangeBar.state.value.endValue
                    )
                )
                invalidate()
            }
        }
    }

    private fun invalidate() {
        epoxyController.setData(viewBinding.rangeBar.state)
    }

    override fun onDestroy() {
        epoxyController.cancelPendingModelBuild()
        super.onDestroy()
    }
}
