package com.dannyjung.rangebar.sample

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.widget.SeekBar
import androidx.constraintlayout.widget.ConstraintLayout
import com.airbnb.epoxy.*
import com.dannyjung.rangebar.sample.databinding.ViewValueControllerBinding

@ModelView(autoLayout = ModelView.Size.MATCH_WIDTH_WRAP_HEIGHT)
class ValueController @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val viewBinding = ViewValueControllerBinding.inflate(context.layoutInflater, this)

    var max: Int? = null
        @ModelProp set

    @TextProp
    fun setTitle(title: CharSequence?) {
        viewBinding.title.text = title
    }

    @ModelProp
    fun setValue(value: Int?) {
        viewBinding.value.text = value.toString()
        viewBinding.seekBar.progress = value ?: 0
    }

    @ModelProp
    fun setPadding(outRect: Rect?) {
        setPadding(
            outRect?.left ?: 0,
            outRect?.top ?: 0,
            outRect?.right ?: 0,
            outRect?.bottom ?: 0
        )
    }

    @AfterPropsSet
    fun onAfterPropsSet() {
        viewBinding.seekBar.max = max ?: 100
    }

    @CallbackProp
    fun setOnSeekBarChanged(onChanged: ((progress: Int) -> Unit)?) {
        viewBinding.seekBar.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    onChanged?.invoke(progress)
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit

                override fun onStopTrackingTouch(seekBar: SeekBar?) = Unit
            }
        )
    }
}
