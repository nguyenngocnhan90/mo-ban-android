package com.moban.util

import android.content.Context
import com.moban.R
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by neal on 3/7/18.
 */
class DateUtil {

    companion object {
        const val DF_SIMPLE_STRING = "dd/MM/yyyy"
        const val DF_FULL_DATE_STRING = "HH:mm dd/MM/yyyy"

        private val calendar = Calendar.getInstance()

        private fun simpleDateFormat(format: String): SimpleDateFormat {
            return SimpleDateFormat(format, Locale.US)
        }

        fun dateFromSeconds(seconds: Long): Date {
            calendar.timeInMillis = seconds * 1000
            return calendar.time
        }

        fun dateStringFromDate(date: Date, format: String): String {
            return simpleDateFormat(format).format(date)
        }

        fun dateStringFromSeconds(seconds: Long, format: String): String {
            val date = dateFromSeconds(seconds)
            return simpleDateFormat(format).format(date)
        }

        fun getPastTimeString(seconds: Long, context: Context): String {
            val now = Date().time / 1000
            val offset = now - seconds

            val time: String
            val minute = context.getString(R.string.minute)
            val hour = context.getString(R.string.hour)
            val day = context.getString(R.string.day)
            val month = context.getString(R.string.month)
            val year = context.getString(R.string.year)

            if (offset < 60) {
                return context.getString(R.string.just_done)
            } else if (offset < 60 * 60) {
                time = (offset / 60).toString() + " " + minute
            } else if (offset < 60 * 60 * 24) {
                time = (offset / 60 / 60).toString() + " " + hour
            } else if (offset < 60 * 60 * 24 * 30) {
                time = (offset / 60 / 60 / 24).toString() + " " + day
            } else if (offset < 60 * 60 * 24 * 365) {
                time = (offset / 60 / 60 / 24 / 30).toString() + " " + month
            } else {
                time = (offset / 60 / 60 / 24 / 365).toString() + " " + year
            }

            return time + " " + context.getString(R.string.ago)
        }

        fun currentTimeSeconds(): Long {
            return System.currentTimeMillis() / 1000
        }

        fun isEndOfMonth(): Boolean {
            val today: Calendar = Calendar.getInstance()
            return today.get(Calendar.DAY_OF_MONTH) == today.getActualMaximum(Calendar.DAY_OF_MONTH)
        }
    }

}
