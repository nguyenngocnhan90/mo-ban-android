package com.moban.extension

import java.text.DecimalFormat

fun Double.toFormatString(): String {
    val decimalFormat = DecimalFormat("#.#")
    return decimalFormat.format(this)
}

fun Double.format(digits: Int) = "%.${digits}f".format(this)
