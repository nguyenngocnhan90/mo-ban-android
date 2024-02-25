package com.moban.enum

import android.content.Context
import com.moban.R

/**
 * Created by H. Len Vo on 9/29/18.
 */
enum class LinkmartSortType(val type: String) {
    SALE("sale"),
    FEATURE("feature"),
    NEWEST("newest");

    companion object {
        fun from(type: String): LinkmartSortType {
            return when(type) {
                "sale" -> SALE
                "feature" -> FEATURE
                else -> NEWEST
            }
        }

        fun getSortType(): Array<LinkmartSortType> {
            return arrayOf(SALE, FEATURE, NEWEST)
        }
    }

    fun toString(context: Context): String {
        return when(type) {
            "sale" -> context.getString(R.string.sort_sale)
            "feature" -> context.getString(R.string.sort_hot)
            else -> context.getString(R.string.sort_latest)
        }
    }
}
