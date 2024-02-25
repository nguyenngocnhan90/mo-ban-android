package com.moban.enum


/**
 * Created by H. Len Vo on 4/29/20.
 * Copyright © 2019 H. Len Vo. All rights reserved.
 */
enum class ProjectHighlightType(val value: Int) {
    NONE(0),
    F1(1),
    F2(2),
    RECENT(3);

    companion object {
        fun from(value: Int): ProjectHighlightType = values().first { it.value == value }
    }
}
