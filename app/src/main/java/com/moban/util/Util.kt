package com.moban.util

import android.content.Context
import android.os.Build
import android.util.TypedValue
import androidx.core.content.ContextCompat

/**
 * Created by neal on 3/3/18.
 */
class Util {
    companion object {
        fun getColor(context: Context, id: Int): Int {
            val version = Build.VERSION.SDK_INT
            return if (version >= 23) {
                ContextCompat.getColor(context, id)
            } else {
                context.resources.getColor(id)
            }
        }

        fun saveToClipboard(context: Context, text: String) {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
            val clip = android.content.ClipData.newPlainText("Copied Text", text)
            clipboard.setPrimaryClip(clip)
        }

        fun convertDpToPx(context: Context, dp: Int): Int {
            return convertDpToPx(context, dp.toFloat())
        }

        fun convertDpToPx(context: Context, dp: Float): Int {
            if (dp == 0f) return 0
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dp,
                context.resources.displayMetrics).toInt()
        }
    }
}
