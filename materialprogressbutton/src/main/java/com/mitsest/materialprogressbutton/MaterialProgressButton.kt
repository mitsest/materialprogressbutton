package com.mitsest.materialprogressbutton

import android.animation.AnimatorSet
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.TextView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.google.android.material.button.MaterialButton
import com.mitsest.functional.*


open class MaterialProgressButton : MaterialButton {
    private enum class MaterialProgressButtonState { DEFAULT, LOADING }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private var progressDrawable: CircularProgressDrawable? = null
    private var progressDrawableCallback: Drawable.Callback? = null
    private var materialProgressButtonState = MaterialProgressButtonState.DEFAULT
    private var initialText: CharSequence? = null
    private var initialLayoutParams: ViewGroup.LayoutParams? = null
    private val initialTextColor by lazy { currentTextColor }
    private val initialBackgroundColor by lazy { backgroundTintList?.defaultColor }
    private var currentAnimation: AnimatorSet? = null

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        setShowProgress(materialProgressButtonState == MaterialProgressButtonState.LOADING)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        Unit.pipe(clearProgressDrawable().curry(clearProgressDrawableCallback()))
    }

    fun setShowProgress(showProgress: Boolean) {
        Unit.pipe(
            `if`(
                Pair(
                    showProgress.toCallback(),
                    cancelAnimatorSet<Unit>(currentAnimation)
                        .curry(getCurrentLayoutParams())
                        .curry(setInitialLayoutParams())
                        .curry(fixLayoutParamsSize(width, height))
                        .curry(setInitialText(text))
                        .curry(disable())
                        .curry(setMaterialProgressButtonState(MaterialProgressButtonState.LOADING))
                        .curry(
                            showProgress(
                                this,
                                currentTextColor,
                                backgroundTintList?.defaultColor,
                                SHOW_PROGRESS_ANIMATION_DURATION,
                            )
                        )
                        .curry(setStateOnShowProgress())
                        .runIf(materialProgressButtonState == MaterialProgressButtonState.DEFAULT)
                ),
                `else` = getCurrentViewChain()
                    .curry(cancelAnimatorSet(currentAnimation))
                    .curry(
                        hideProgress(
                            this,
                            initialTextColor,
                            initialBackgroundColor,
                            onHideProgressEnd = restoreLayoutParams(initialLayoutParams),
                            onShowTextEnd = clearProgressDrawable()
                                .curry(clearProgressDrawableCallback())
                                .curry(setMaterialProgressButtonState(MaterialProgressButtonState.DEFAULT))
                                .curry(enable()),
                            animationDuration = HIDE_PROGRESS_ANIMATION_DURATION
                        )
                    )
                    .curry(setAnimatorSet())
                    .runIf(materialProgressButtonState == MaterialProgressButtonState.LOADING)
            )
        )
    }

    private fun setAnimatorSet(): (AnimatorSet) -> AnimatorSet = { animatorSet ->
        this.currentAnimation = animatorSet
        animatorSet
    }

    private fun getCurrentLayoutParams(): (Any) -> ViewGroup.LayoutParams = {
        layoutParams
    }

    private fun setInitialLayoutParams(): (ViewGroup.LayoutParams) -> ViewGroup.LayoutParams =
        { layoutParams ->
            initialLayoutParams = layoutParams
            layoutParams
        }

    private fun setInitialText(text: CharSequence?): (Any) -> Unit = {
        initialText = text
    }

    private fun setMaterialProgressButtonState(state: MaterialProgressButtonState): (Any) -> Unit =
        {
            materialProgressButtonState = state
        }


    private fun saveProgressDrawableCallback(): (Pair<CircularProgressDrawable?, Drawable.Callback?>) -> CircularProgressDrawable? =
        { pair ->
            val (progressDrawable, progressDrawableCallback) = pair
            this.progressDrawableCallback = progressDrawableCallback
            progressDrawable
        }

    private fun clearProgressDrawable(): (Any) -> Unit = {
        progressDrawable?.stop()
        progressDrawable = null
    }

    private fun clearProgressDrawableCallback(): (Unit) -> Unit = {
        progressDrawableCallback = null
    }

    private fun setProgressDrawable(): (CircularProgressDrawable?) -> CircularProgressDrawable? =
        { progressDrawable ->
            this.progressDrawable = progressDrawable
            progressDrawable
        }

    private fun getCurrentViewChain(): (Any) -> AnimatedViewChain =
        {
            AnimatedViewChain(progressDrawable, initialText, null)
        }

    private fun setStateOnShowProgress(): (Pair<AnimatedViewChain, AnimatorSet>) -> Unit = { pair ->
        val (viewChain, animatorSet) = pair
        val (progressDrawable, _, _) = viewChain

        progressDrawable.pipe(setProgressDrawable())
        Pair(progressDrawable, progressDrawable?.callback).pipe(
            saveProgressDrawableCallback()
        )
        animatorSet.pipe((setAnimatorSet()))
    }

    companion object {
        const val SHOW_PROGRESS_ANIMATION_DURATION = 200L
        const val HIDE_PROGRESS_ANIMATION_DURATION = 250L
    }
}

@Suppress("SameParameterValue")
private fun showProgress(
    textView: TextView,
    initialTextColor: Int,
    initialBackgroundColor: Int?,
    animationDuration: Long
): (Any) -> Pair<AnimatedViewChain, AnimatorSet> =
    initProgressDrawable(textView.context)
        .curry(setProgressDrawableColor(initialTextColor))
        .curry(getProgressDrawableSize())
        .curry(setProgressDrawableBounds())
        .compose(initProgressDrawableCallback(textView))
        .curry(setProgressDrawableCallback())
        .curry(createImageSpanFromProgressDrawable())
        .curry(createSpannableFromImageSpan())
        .curryCompose(
            getTextColorAnimator(
                textView,
                from = initialTextColor,
                to = initialBackgroundColor
            )
                .composeList(
                    getProgressDrawableColorAnimator(
                        from = initialBackgroundColor,
                        to = initialTextColor
                    )
                        .curry(setOnAnimationStart(
                            textView.setTextFromSpannable()
                                .curry(startProgressDrawable())
                        ))
                )
                .curry(getSequentialAnimatorSet(animationDuration))
                .curry(startAnimatorSet())
        )

@Suppress("SameParameterValue")
private fun hideProgress(
    textView: TextView,
    initialTextColor: Int,
    initialBackgroundColor: Int?,
    onHideProgressEnd: (Pair<CircularProgressDrawable?, CharSequence?>) -> Unit,
    onShowTextEnd: (Any) -> Unit,
    animationDuration: Long
): (AnimatedViewChain) -> AnimatorSet =
    getProgressDrawableColorAnimator(
        from = initialTextColor,
        to = initialBackgroundColor
    )
        .curry(
            setOnAnimationEnd(onHideProgressEnd)
                .composeList(
                    getTextColorAnimator(
                        textView,
                        from = initialBackgroundColor,
                        to = initialTextColor
                    )
                        .curry(
                            setOnAnimationEnd(
                                textView.setTextFromSpannable().curry(onShowTextEnd)
                            )
                        )
                )
                .curry(getSequentialAnimatorSet(animationDuration))
                .curry(startAnimatorSet())
        )