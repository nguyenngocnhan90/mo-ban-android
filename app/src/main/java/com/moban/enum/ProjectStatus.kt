package com.moban.enum

import android.content.Context
import android.graphics.drawable.Drawable
import com.moban.R

enum class ProjectStatus(val value: Int) {
    none(0),
    selling(1),
    soldOut(2),
    commingSoon(3),
    hot(100),
    slk(200);

    fun color(context: Context): Drawable? {
        return when (this) {
            selling -> context.getDrawable(R.drawable.background_half_round_linkhouse_button)
            slk -> context.getDrawable(R.drawable.background_half_round_linkhouse_button)
            soldOut -> context.getDrawable(R.drawable.background_half_round_gray_button)
            commingSoon -> context.getDrawable(R.drawable.background_half_round_linkmart_button)
            hot -> context.getDrawable(R.drawable.background_half_round_red_button)
            else -> context.getDrawable(R.drawable.background_border_dark_corner_5)
        }
    }

    companion object {
        fun from(value: Int): ProjectStatus? {
            return values().firstOrNull { it.value == value }
        }
    }
}
