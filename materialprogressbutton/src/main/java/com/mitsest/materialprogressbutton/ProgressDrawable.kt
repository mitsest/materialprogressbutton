package com.mitsest.materialprogressbutton

import VerticalImageSpan
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ImageSpan
import android.view.View
import android.widget.TextView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable

internal fun initProgressDrawable(context: Context): (Any) -> CircularProgressDrawable = {
    CircularProgressDrawable(context).apply {
        setStyle(CircularProgressDrawable.DEFAULT)
    }
}

internal fun setProgressDrawableColor(ProgressDrawableColor: Int?): (CircularProgressDrawable) -> CircularProgressDrawable =
    { progressDrawable ->
        progressDrawable.setColorSchemeColors(ProgressDrawableColor!!)
        progressDrawable
    }

internal fun getProgressDrawableSize(): (CircularProgressDrawable) -> Pair<CircularProgressDrawable, Int> =
    { progressDrawable ->
        Pair(
            progressDrawable,
            (progressDrawable.centerRadius + progressDrawable.strokeWidth).toInt() * 2
        )
    }

internal fun setProgressDrawableBounds(): (Pair<CircularProgressDrawable, Int>) -> CircularProgressDrawable =
    { pair ->
        val (progressDrawable, size) = pair
        progressDrawable.setBounds(0, 0, size, size)
        progressDrawable
    }

internal fun initProgressDrawableCallback(view: View?): (Any) -> Drawable.Callback = {
    object : Drawable.Callback {
        override fun invalidateDrawable(who: Drawable) {
            view?.invalidate()
        }

        override fun scheduleDrawable(who: Drawable, what: Runnable, `when`: Long) {}
        override fun unscheduleDrawable(who: Drawable, what: Runnable) {}
    }
}

internal fun setProgressDrawableCallback(): (Pair<CircularProgressDrawable?, Drawable.Callback>) -> Pair<CircularProgressDrawable?, Drawable.Callback?> =
    { pair ->
        val (progressDrawable, progressDrawableCallback) = pair
        progressDrawable?.callback = progressDrawableCallback
        pair
    }


@SuppressLint("InlinedApi")
internal fun createImageSpanFromProgressDrawable(): (Pair<CircularProgressDrawable?, Drawable.Callback?>) -> Pair<CircularProgressDrawable?, ImageSpan?> =
    { pair ->
        val (progressDrawable, _) = pair

        if (progressDrawable != null) {
            Pair(progressDrawable, VerticalImageSpan(progressDrawable))
        } else {
            Pair(progressDrawable, null)
        }
    }

internal fun createSpannableFromImageSpan(): (Pair<CircularProgressDrawable?, ImageSpan?>) -> AnimatedViewChain =
    { pair ->
        val (progressDrawable, imageSpan) = pair
        val spannable = SpannableString(" ")
        spannable.setSpan(imageSpan, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        AnimatedViewChain(progressDrawable, spannable, null)
    }

internal fun startProgressDrawable(): (Pair<CircularProgressDrawable?, CharSequence?>) -> Pair<CircularProgressDrawable?, CharSequence?> =
    { pair ->
        val (progressDrawable, _) = pair
        progressDrawable?.start()
        pair
    }

internal fun TextView.setTextFromSpannable(): (Pair<CircularProgressDrawable?, CharSequence?>) -> Pair<CircularProgressDrawable?, CharSequence?> =
    { pair ->
        val (_, spannableString) = pair
        text = spannableString
        pair
    }
