package com.moban.enum

import android.content.Context
import android.graphics.drawable.Drawable
import com.moban.R

/**
 * Created by LenVo on 3/30/18.
 */
enum class DealStatus(val value: Int) {
    BOOKED(1),
    CANCELED(2),
    DEPOSITED(3),
    CANCELED_DEPOSITED(4),
    CONTRACTED(5),
    FEE01(11),
    FEE02(12),
    FEE03(13),
    FEE_COMPLETED(20),
    COMMISSION(30);

    companion object {
        fun from(value: Int): DealStatus = values().first { it.value == value }
        fun fromNullable(value: Int): DealStatus? = values().firstOrNull { it.value == value }
    }

    fun getBackgroundCircleImg(context: Context): Drawable {
        return context.getDrawable( when (this) {
            BOOKED -> R.drawable.circle_icon_booked_background
            CANCELED -> R.drawable.circle_icon_cancel_background
            DEPOSITED -> R.drawable.circle_icon_deposited_background
            CANCELED_DEPOSITED -> R.drawable.circle_icon_cancel_background
            else -> R.drawable.circle_icon_contracted_background
        })!!
    }

    fun getText(context: Context): String {
        return context.getString(when (this) {
            BOOKED -> R.string.deal_booked
            CANCELED -> R.string.deal_cancel_booked
            DEPOSITED -> R.string.deal_deposited
            CANCELED_DEPOSITED -> R.string.deal_cancel_deposited
            FEE01 -> R.string.deal_cancel_fee_01
            FEE02 -> R.string.deal_cancel_fee_02
            FEE03 -> R.string.deal_cancel_fee_03
            FEE_COMPLETED -> R.string.deal_cancel_fee_completed
            COMMISSION -> R.string.deal_cancel_commission
            else -> R.string.deal_contracted
        })
    }

    fun getCircleImg(context: Context): Drawable {
        return context.getDrawable(when (this) {
            BOOKED -> R.drawable.circle_icon_booked
            CANCELED -> R.drawable.circle_icon_cancel
            DEPOSITED -> R.drawable.circle_icon_booked
            CANCELED_DEPOSITED -> R.drawable.circle_icon_cancel
            else -> R.drawable.circle_icon_contracted
        })!!
    }

    fun getBackgroundRound15(context: Context): Drawable {
        return context.getDrawable(when (this) {
            BOOKED -> R.drawable.background_booked_round_15
            CANCELED -> R.drawable.background_cancel_round_15
            DEPOSITED -> R.drawable.background_booked_round_15
            CANCELED_DEPOSITED -> R.drawable.background_cancel_round_15
            else -> R.drawable.background_contracted_round_15
        })!!
    }

    fun getBackgroundRound5(context: Context): Drawable {
        return context.getDrawable(when (this) {
            BOOKED -> R.drawable.background_booked_round_5
            CANCELED -> R.drawable.background_cancel_round_5
            DEPOSITED -> R.drawable.background_booked_round_5
            CANCELED_DEPOSITED -> R.drawable.background_cancel_round_5
            else -> R.drawable.background_contracted_round_5
        })!!
    }
}
