package com.moban.enum

enum class DealFilter(val value: Int) {
    ALL(0),
    WAITING(1),
    APPROVED(2),
    REJECTED(3),
    REVIEWABLE(4),
    REVIEWED(5);
}
