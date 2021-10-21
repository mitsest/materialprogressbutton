package com.mitsest.materialprogressbutton

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.widget.TextView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.mitsest.functional.pipe


internal fun <T> setOnAnimationListeners(
    valueAnimator: ValueAnimator?,
    onAnimationEnd: ((T) -> Any)? = null,
    onAnimationStart: ((T) -> Any)? = null
): (T) -> T =
    { args ->
        valueAnimator?.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator?) {
                onAnimationStart?.invoke(args)
            }

            override fun onAnimationEnd(animation: Animator?) {
                onAnimationEnd?.invoke(args)
            }

            override fun onAnimationCancel(animation: Animator?) {
                onAnimationEnd?.invoke(args)
            }

            override fun onAnimationRepeat(animation: Animator?) {}
        })
        args
    }

@SuppressLint("Recycle")
internal fun getTextColorAnimator(
    textView: TextView,
    from: Int?,
    to: Int?,
    duration: Long? = null
): (AnimatedViewChain) -> AnimatedViewChain = { triple ->
    val (progressDrawable, spannableString, _) = triple

    AnimatedViewChain(
        progressDrawable,
        spannableString,
        ValueAnimator.ofObject(ArgbEvaluator(), from, to).apply {
            duration?.let { this.duration = it }
            addUpdateListener { textView.setTextColor(it.animatedValue as Int) }
        })
}

@SuppressLint("Recycle")
internal fun getProgressDrawableColorAnimator(
    from: Int?,
    to: Int?
): (AnimatedViewChain) -> AnimatedViewChain =
    { triple ->
        val (progressDrawable, spannableString, _) = triple
        AnimatedViewChain(
            progressDrawable,
            spannableString,
            ValueAnimator.ofObject(ArgbEvaluator(), from, to).apply {
                addUpdateListener {
                    progressDrawable?.colorFilter =
                        PorterDuffColorFilter(animatedValue as Int, PorterDuff.Mode.MULTIPLY)
                }
            })
    }

@Suppress("SameParameterValue")
internal fun getSequentialAnimatorSet(duration: Long): (List<AnimatedViewChain>) -> AnimatorSet =
    { valueAnimatorChains ->
        AnimatorSet().apply {
            playSequentially(
                valueAnimatorChains.asSequence().map { it.valueAnimator() }.toList()
            )
            this.duration = duration
        }
    }


internal fun startAnimatorSet(): (AnimatorSet) -> AnimatorSet = { animatorSet ->
    animatorSet.start()
    animatorSet
}

internal fun <T> cancelAnimatorSet(animatorSet: AnimatorSet?): (T) -> T = {
    animatorSet?.cancel()
    it
}

internal fun setOnAnimationStart(
    onAnimationStart: (Pair<CircularProgressDrawable?, CharSequence?>) -> Pair<CircularProgressDrawable?, CharSequence?>
): (AnimatedViewChain) -> AnimatedViewChain =
    {
        val (progressDrawable, spannableString, valueAnimator) = it
        Pair(progressDrawable, spannableString).pipe(
            setOnAnimationListeners(
                valueAnimator,
                onAnimationStart = onAnimationStart
            )
        )
        it
    }


internal fun setOnAnimationEnd(
    onAnimationEnd: (Pair<CircularProgressDrawable?, CharSequence?>) -> Unit
): (AnimatedViewChain) -> AnimatedViewChain =
    {
        val (progressDrawable, spannableString, valueAnimator) = it
        Pair(progressDrawable, spannableString).pipe(
            setOnAnimationListeners(
                valueAnimator,
                onAnimationEnd = onAnimationEnd
            )
        )
        it
    }

