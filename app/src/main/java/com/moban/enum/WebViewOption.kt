package com.moban.enum

enum class WebViewOption(val value: Int) {
    NONE(0),
    DELETE_ACCOUNT(1);

    companion object {
        fun from(value: Int): WebViewOption? = WebViewOption.values().firstOrNull { it.value == value }
    }
}