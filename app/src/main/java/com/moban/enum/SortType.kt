package com.moban.enum

import android.content.Context
import com.moban.R

/**
 * Created by H. Len Vo on 8/27/18.
 */
enum class SortType(val type: String) {
    LATEST("latest"),
    RATE_ASC("rate_asc"),
    RATE_DESC("rate_desc"),
    HOT("hot"),
    REWARD("reward");

    companion object {
        fun getSortTypeProject(): Array<SortType> {
            return arrayOf(HOT, LATEST, REWARD, RATE_ASC, RATE_DESC)
        }
    }


    fun getString(context: Context): String {
        return when(this) {
            RATE_ASC -> {
                context.getString(R.string.sort_rate_asc)
            }
            RATE_DESC -> {
                context.getString(R.string.sort_rate_desc)
            }
            HOT -> {
                context.getString(R.string.sort_hot)
            }
            REWARD -> {
                context.getString(R.string.sort_reward)
            }
            else -> context.getString(R.string.sort_latest)
        }
    }

    fun getImageSourceId(): Int {
        return when(this) {
            RATE_ASC -> {
                R.drawable.sort_down
            }
            RATE_DESC -> {
                R.drawable.sort_up
            }
            HOT -> {
                R.drawable.sort_hot
            }
            REWARD -> {
                R.drawable.sort_reward
            }
            else -> return R.drawable.sort_newest
        }
    }
}
