package com.moban.enum

import android.content.Context
import android.graphics.drawable.Drawable
import com.moban.R

/**
 * Created by LenVo on 3/30/18.
 */
enum class FlatStatus(val value: Int) {
    EMPTY(0),
    BOOK(1),
    BOOKED(2),
    DEPOSITED(3),
    CONTRACTED(4);

    companion object {
        fun from(value: Int): FlatStatus = FlatStatus.values().first { it.value == value }
    }


//    fun getText(context: Context): String {
//        return when (this) {
//            FlatStatus.EMPTY -> context.getString(R.string.project_detail_cart_empty)
//            FlatStatus.BOOK -> context.getString(R.string.project_detail_cart_book)
//            FlatStatus.BOOKED -> context.getString(R.string.project_detail_cart_booked)
//            FlatStatus.DEPOSITED -> context.getString(R.string.project_detail_cart_deposit)
//            else -> context.getString(R.string.project_detail_cart_contract)
//        }
//    }

    fun getBackgroundRound15(context: Context): Drawable? {
        return when (this) {
            FlatStatus.EMPTY -> context.getDrawable(R.drawable.background_empty_round_15)!!
            FlatStatus.BOOK -> context.getDrawable(R.drawable.background_book_round_15)!!
            FlatStatus.BOOKED -> context.getDrawable(R.drawable.background_booked_round_15)!!
            FlatStatus.DEPOSITED -> context.getDrawable(R.drawable.background_booked_round_15)!!
            else -> context.getDrawable(R.drawable.background_contracted_round_15)!!
        }
    }
}
