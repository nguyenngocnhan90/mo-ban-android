package com.moban.enum


/**
 * Created by H. Len Vo on 5/25/20.
 * Copyright © 2019 H. Len Vo. All rights reserved.
 */
enum class HomeDealType (val value: String) {
    none(""),
    special("special"),
    flashsale("flashsale");

    companion object {
        fun from(value: String): HomeDealType? {
            return values().firstOrNull { it.value == value }
        }
    }
}
