package com.mitsest.materialprogressbutton

import android.animation.ValueAnimator
import androidx.swiperefreshlayout.widget.CircularProgressDrawable

internal typealias AnimatedViewChain = Triple<CircularProgressDrawable?, CharSequence?, ValueAnimator?>

internal fun AnimatedViewChain.valueAnimator() = third
