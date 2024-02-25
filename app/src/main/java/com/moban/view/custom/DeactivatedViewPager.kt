package com.moban.view.custom

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager


/**
 * Created by H. Len Vo on 5/1/20.
 * Copyright © 2019 H. Len Vo. All rights reserved.
 */
class DeactivatedViewPager @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null
) : ViewPager(context, attrs) {

    var isPagingEnabled = true

    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        return isPagingEnabled && super.onTouchEvent(ev)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return isPagingEnabled && super.onInterceptTouchEvent(ev)
    }
}
