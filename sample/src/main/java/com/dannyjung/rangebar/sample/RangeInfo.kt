package com.dannyjung.rangebar.sample

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.widget.LinearLayout
import com.airbnb.epoxy.ModelProp
import com.airbnb.epoxy.ModelView
import com.airbnb.epoxy.TextProp
import com.dannyjung.rangebar.sample.databinding.ViewRangeInfoBinding

@ModelView(autoLayout = ModelView.Size.MATCH_WIDTH_WRAP_HEIGHT)
class RangeInfo @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val viewBinding = ViewRangeInfoBinding.inflate(context.layoutInflater, this)

    init {
        orientation = VERTICAL
    }

    @TextProp
    fun setTitle(title: CharSequence?) {
        viewBinding.title.text = title
    }

    @ModelProp
    fun setValue(value: Int?) {
        viewBinding.value.text = value.toString()
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
}
