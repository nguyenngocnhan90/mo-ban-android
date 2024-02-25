package com.moban.util

import android.content.Context
import com.moban.R
import java.text.DecimalFormat

class NumberUtil {
    companion object {
        fun formatDecimal(number: Double, format: String): String {
            val formatter = DecimalFormat(format)
            return formatter.format(number)
        }

        fun formatPrice(number: Double, context: Context): String {
            return formatDecimal(number,
                    context.getString(R.string.format_full_price_vnd))
        }

        fun formatSimplePrice(number: Double, context: Context): String {
            return formatDecimal(number / 1000.0,
                    context.getString(R.string.format_simple_price))
        }

        fun formatNumber(number: Double, context: Context): String {
            return formatDecimal(number,
                    context.getString(R.string.normal_number_format))
        }

    }
}
