package com.moban.extension

import android.content.Context
import android.util.Patterns
import android.webkit.URLUtil
import com.moban.LHApplication
import java.util.regex.Pattern


fun String.isValidPhoneNumber(): Boolean {
    if (this.toLongOrNull() == null)
        return false
    return this.length in 10..13
}

fun String.isValidEmail(): Boolean {
    return Pattern.compile(
            "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]|[\\w-]{2,}))@"
                    + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                    + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                    + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                    + "[0-9]{1,2}|25[0-5]|2[0-4][0-9]))|"
                    + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$"
    ).matcher(this).matches()
}

fun String.isValidUrl(): Boolean {
    return URLUtil.isValidUrl(this) && Patterns.WEB_URL.matcher(this).matches()
}

fun getString(context: Context, stringId: Int): String {
    return context.getString(stringId)
}

fun String.getLink(): String {
    return this.replace("~", LHApplication.instance.rootUrl)
}
