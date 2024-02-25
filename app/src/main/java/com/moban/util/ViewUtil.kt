package com.moban.util

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.moban.helper.LocalStorage

/**
 * Created by thangnguyen on 12/28/17.
 */
class ViewUtil {
    companion object {
        fun setMarginTop(view: View, marginValue: Int) {
            val layoutParams: ViewGroup.MarginLayoutParams = view.layoutParams as ViewGroup.MarginLayoutParams
            layoutParams.topMargin = marginValue
            view.requestLayout()
        }

        fun getStatusBarHeight(context: Context): Int {
            var result = 0
            val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
            if (resourceId > 0) {
                result = context.resources.getDimensionPixelSize(resourceId)
            }
            return result
        }

        fun updateUnReadNotificationView(navNotification: TextView) {
            val unReadNotification = LocalStorage.getUnReadNotification()
            navNotification.text = unReadNotification.toString()
            navNotification.visibility = if (unReadNotification == 0)
                View.INVISIBLE else View.VISIBLE
        }
    }
}
