package com.moban.util

import android.content.Context
import android.os.Build
import android.text.Html
import androidx.core.content.ContextCompat

/**
 * Created by LenVo on 3/17/18.
 */
class StringUtil {
    companion object {
        fun getColor(context: Context, id: Int): Int {
            val version = Build.VERSION.SDK_INT
            return if (version >= 23) {
                ContextCompat.getColor(context, id)
            } else {
                context.resources.getColor(id)
            }
        }

        fun fromHtml(html: String): String? {
            val spanned = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY)
            } else {
                Html.fromHtml(html)
            }

            return spanned?.toString()
        }
    }
}
