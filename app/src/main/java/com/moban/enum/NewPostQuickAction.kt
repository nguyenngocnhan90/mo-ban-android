package com.moban.enum

enum class NewPostQuickAction(val value: Int) {
    NONE(0),
    PHOTO(1),
    YOUTUBE_LINK(2),
    TOPIC(3);

    companion object {
        fun from(value: Int): NewPostQuickAction = NewPostQuickAction.values().first { it.value == value }
    }
}