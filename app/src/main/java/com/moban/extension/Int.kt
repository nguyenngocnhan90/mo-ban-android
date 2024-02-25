package com.moban.extension

import java.text.NumberFormat
import java.util.*

fun Int.toFullPriceString(): String {
    return NumberFormat.getNumberInstance(Locale.US).format(this)
}

infix fun Int?.orElse(default: Int): Int = if (this == null || this == 0) default else this
