package com.moban.enum

enum class ApproveStatus(val value: Int) {
    Waiting(0),
    Approved(1),
    Denied(2),
    WatingForFixing(3);

    companion object {
        fun from(value: Int): ApproveStatus? = values().firstOrNull { it.value == value }
    }
}
