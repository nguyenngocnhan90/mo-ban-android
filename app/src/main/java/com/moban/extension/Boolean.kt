package com.moban.extension

/**
 * Created by neal on 3/19/18.
 */

val Boolean?.isTrue: Boolean
    get() = this ?: false

val Boolean?.isFalse: Boolean
    get() = this != true

val Boolean.toInt: Int
    get() {
        return if (this) 1 else 0
    }
