package com.moban.enum

import android.content.Context
import android.graphics.drawable.Drawable
import com.moban.R

enum class CartStatus(val value: Int) {
    waitingAdminConfirmation(-2),
    rejected(-1),
    waiting(0),
    accepted(1),
    expired(-99);

    companion object {
        fun from(value: Int): CartStatus? = values().firstOrNull { it.value == value }
    }

    fun color(): Int {
        return when (this) {
                    rejected, expired -> R.color.color_reject_status
                    accepted -> R.color.color_accept_status
                    else -> R.color.color_booking_status
                }
    }

    fun backgroundColor(context: Context): Drawable
    {
        return context.getDrawable(when (this) {
            rejected, expired -> R.drawable.background_project_detail_round_red
            accepted -> R.drawable.background_project_detail_round_booking
            else -> R.drawable.background_project_detail_round_accept
        })!!
    }
}
