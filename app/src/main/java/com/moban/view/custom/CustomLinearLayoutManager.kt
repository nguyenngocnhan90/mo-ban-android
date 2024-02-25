package com.moban.view.custom

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager

class CustomLinearLayoutManager(context: Context) : LinearLayoutManager(context) {
    var isScrollEnabled = true

    override fun canScrollVertically(): Boolean {
        return isScrollEnabled && super.canScrollVertically()
    }

    override fun canScrollHorizontally(): Boolean {
        return isScrollEnabled && super.canScrollHorizontally()
    }
}
