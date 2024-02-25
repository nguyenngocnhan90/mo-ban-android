package com.moban.enum

/**
 * Created by neal on 3/21/18.
 */
enum class NotificationType(val value: String) {
    FEED("Feed"),
    PROJECT("Project"),
    TRANSACTION("Transaction"),
    BOOKING("Booking"),
    DEAL("Deal"),
    MISSION("Mission"),
    TRANSACTION_COIN("Transaction/Transfer_Coin"),
    REVIEW_DEAL("Notification/ReviewDeal"),
    REVIEW("Review/Detail"),
    REWARD_DETAIL("Reward/Detail")
}
