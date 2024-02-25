package com.moban.util

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

/**
 * Created by neal on 3/17/18.
 */
class KeyboardUtil {
    companion object {
        fun hideKeyboard(activity: Activity) {
            val inputManager = activity
                    .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

            // check if no view has focus:
            val view = activity.getCurrentFocus()
            if (view != null) {
                inputManager.hideSoftInputFromWindow(view.getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS)
            }
        }

        fun forceHideKeyboard(activity: Activity) {
            val view = activity.getCurrentFocus()
            if (view != null) {
                val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0)
            }
        }

        fun showKeyboard(activity: Activity) {

            val m = activity
                    .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            m.toggleSoftInput(0, InputMethodManager.SHOW_IMPLICIT)
        }

        fun showKeyboard(activity: Activity, view: View) {
            view.requestFocus()
            val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    interface KeyboardVisibilityListener {
        fun onKeyboardVisibilityChanged(isShow: Boolean, keyboardHeight: Int)
    }

}
