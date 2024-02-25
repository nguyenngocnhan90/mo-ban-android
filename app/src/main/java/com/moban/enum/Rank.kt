package com.moban.enum

import android.content.Context
import com.moban.R

enum class Rank(val value: Int) {
    MEMBER(0),
    PARTNER(1),
    PARTNER_MANAGER(2),
    DIRECTOR(3);

    fun color(): String {
        return "#" +
                when (this) {
                    PARTNER -> "00A34C"
                    PARTNER_MANAGER -> "EAAB33"
                    DIRECTOR -> "207FD1"
                    else -> "00D664"
                }
    }

    companion object {
        val list: List<Rank> = arrayOf(MEMBER, PARTNER, PARTNER_MANAGER, DIRECTOR).toList()

        fun getRankName(rank: Int, context: Context): String {
            val rankArr = arrayOf(context.getString(R.string.reward_level_member), context.getString(R.string.reward_level_partner),
                    context.getString(R.string.reward_level_partner_manager), context.getString(R.string.reward_level_director))
            return rankArr[rank]
        }
    }
}
