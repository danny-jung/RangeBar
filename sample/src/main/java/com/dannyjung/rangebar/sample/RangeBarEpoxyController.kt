package com.dannyjung.rangebar.sample

import com.airbnb.epoxy.EpoxyController
import com.airbnb.epoxy.TypedEpoxyController
import com.dannyjung.rangebar.RangeBarState

class RangeBarEpoxyController(
    private val buildModelsCallback: EpoxyController.(data: RangeBarState) -> Unit
) : TypedEpoxyController<RangeBarState>() {

    override fun buildModels(data: RangeBarState) {
        buildModelsCallback(data)
    }
}
