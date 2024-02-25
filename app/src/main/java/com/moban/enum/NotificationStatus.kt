package com.moban.enum

/**
 * Created by LenVo on 5/11/18.
 */

enum class NotificationStatus(val value: Int) {
    UNREAD(0),
    READ(1);

    fun backgroundColor(): String {
        return "#" +
                when (this) {
                    UNREAD -> "F5A623"
                    READ -> "4F9C3A"
                    else -> "F5A623"
                }
    }

    companion object {
        val list: List<NotificationStatus> = arrayOf(UNREAD, READ).toList()
    }
}
