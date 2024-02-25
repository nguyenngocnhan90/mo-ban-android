package com.moban.enum


/**
 * Created by H. Len Vo on 4/29/20.
 * Copyright © 2019 H. Len Vo. All rights reserved.
 */
enum class SearchForType(val value: Int) {
    NONE(0),
    RESERVATION(1),
    BOOKING(2);

    companion object {
        fun from(value: Int): SearchForType = values().first { it.value == value }
    }
}
