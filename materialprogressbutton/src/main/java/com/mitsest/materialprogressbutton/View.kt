package com.mitsest.materialprogressbutton

import android.view.View
import android.view.ViewGroup

internal fun View.restoreLayoutParams(initialLayoutParams: ViewGroup.LayoutParams?): (Any) -> (Unit) =
    {
        initialLayoutParams?.let { layoutParams = it }
    }


internal fun fixLayoutParamsSize(
    width: Int,
    height: Int
): (ViewGroup.LayoutParams) -> (ViewGroup.LayoutParams) = { layoutParams ->
    layoutParams.let {
        it.width = width
        it.height = height
    }
    layoutParams
}

internal fun View.disable(): (Any) -> Unit = {
    isClickable = false
}

internal fun View.enable(): (Unit) -> Unit = {
    isClickable = true
}
