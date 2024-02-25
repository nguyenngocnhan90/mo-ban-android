package com.moban.view.custom

import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.animation.DecelerateInterpolator
import android.widget.RelativeLayout
import androidx.annotation.Keep
import com.moban.R
import com.moban.extension.orElse
import kotlinx.android.synthetic.main.view_level_progress.view.*
import java.text.DecimalFormatSymbols

/**
 * Created by thangnguyen on 3/26/19.
 */
class LevelProgressView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {
    private val progressPadding by lazy { convertDpToPx(context, 0) }
    private val staticPadding by lazy { convertDpToPx(context, 40) }
    private var progress = 0f

    var start: Int = 0
        set(value) {
            field = value
        }
    var end: Int = 100
        set(value) {
            field = if (value == 0) 1 else value
        }
    private var current: Int = 0
        set(value) {
            field = value
            this.post {
                level_progress_txtStartPoint.text = "Đã bán ${fastFormatNumber(field)}/${fastFormatNumber(end)}"
            }
        }

    init {
        val layoutInflater = LayoutInflater.from(context)
        layoutInflater.inflate(R.layout.view_level_progress, this, true)
        // load percent
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.LevelProgressView)
        try {
            val current = typedArray.getInt(R.styleable.LevelProgressView_currentPoint, 0)
            setCurrentValue(current)
        } finally {
            typedArray.recycle()
        }
    }

    @Keep
    fun setProgress(progress: Float) {
        if (this.progress != progress) {
            this.progress = progress
            val fullChangeWidth = width - progressPadding - 2 * staticPadding
            if (fullChangeWidth > 0) {
                val layoutParams = level_progress_vProgress.layoutParams
                if (progress == 1f) {
                    layoutParams.width = width - progressPadding
                } else {
                    layoutParams.width = staticPadding + (fullChangeWidth * progress).toInt()
                }
                level_progress_vProgress.layoutParams = layoutParams
            }
        }
    }

    private fun setCurrentValue(current: Int) {
        this.current = current
        setProgress(getProgressBaseOnCurrent(this.current))
    }

    /**
     * Set the progress with an animation.
     * Note that the [android.animation.ObjectAnimator] Class automatically set the progress
     * so don't call the [LevelProgressView.setProgress] directly within this method.
     *
     * @param current The progress it should animate to it.
     */
    fun setCurrentWithAnimation(current: Int, start: Int, end: Int) {
        if (this.start != start || this.end != end || this.current != current) {
            this.start = start
            this.end = end
            this.current = current
            val percent = getProgressBaseOnCurrent(this.current)
            val objectAnimator = ObjectAnimator.ofFloat(this, "progress", percent)
            objectAnimator.duration = 1
            objectAnimator.interpolator = DecelerateInterpolator()
            objectAnimator.start()
        }
    }

    private fun getProgressBaseOnCurrent(current: Int): Float {
        if (current >= end) return 1f
        if (current <= start) return 0f
        val length = end - start orElse 1
        val currentMod = if (current > start) current - start else 0
        return currentMod.toFloat() / length
    }

    fun convertDpToPx(context: Context, dp: Int): Int {
        return convertDpToPx(context, dp.toFloat())
    }

    fun convertDpToPx(context: Context, dp: Float): Int {
        if (dp == 0f) return 0
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                context.resources.displayMetrics).toInt()
    }

    fun fastFormatNumber(number: Int): String {
        if (number < 1000 && number > -1000) return number.toString()

        val startRequiredCount = if (number > 0) 0 else 1
        val charArray = number.toString().toCharArray()
        val originalSize = charArray.size
        val size = originalSize + (originalSize - 1 - startRequiredCount) / 3
        val resultArray = CharArray(size)
        var iRes = size - 1
        var iSeparate = 0
        for (i in (originalSize-1) downTo 0) {
            resultArray[iRes] = charArray[i]
            iRes--
            iSeparate++
            if (iSeparate == 3 && iRes > startRequiredCount) {
                iSeparate = 0
                resultArray[iRes] = DecimalFormatSymbols.getInstance().groupingSeparator
                iRes--
            }
        }

        return String(resultArray)
    }
}
