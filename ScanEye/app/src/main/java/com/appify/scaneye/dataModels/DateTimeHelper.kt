package com.appify.scaneye.dataModels

import android.util.Log
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*

class DateTimeHelper {
    companion object {
        private val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
        private val dateTimeFormatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.ENGLISH)
        private val timeFormatter = SimpleDateFormat("HH:mm", Locale.ENGLISH)

        // get the today's date
        fun getCurrentDate(): String {
            val x = dateTimeFormatter.format(Calendar.getInstance().time).toString()
            Log.e("INSIDE_DATETIME_HELPER : GET_CURRENT_DATE", x)
            return x
        }

        fun getOnlyTime(givenTime: String) : String? {
            return dateTimeFormatter.parse(givenTime)?.let { timeFormatter.format(it) }
        }

        // comparing the dates to get whether the
        // date passed is yesterday, today or older
        // than the yesterday
        fun compareMyDates(givenDate: String, todayDate: String): Int? {
            val givenDateParsed = dateFormatter.parse(givenDate)
            val todayDateParsed = dateFormatter.parse(todayDate)

            when (givenDateParsed?.compareTo(todayDateParsed)) {
                0 -> {
                    return 0
                }
                1 -> {
                    return null
                }
                -1 -> {
                    // when the compared date gives -1
                    // we will now check whether its yesterday or the day before
                    // the yesterday
                    val yestPassed = getYesterdayDate()
                    return if (yestPassed != null && givenDateParsed.compareTo(yestPassed) == 0) -1
                    else -2
                }
            }

            return null
        }

        private fun getYesterdayDate(): Date? {
            val calendarInst = Calendar.getInstance(); calendarInst.add(Calendar.DATE, -1)
            return dateFormatter.parse(dateFormatter.format(calendarInst.time))
        }
    }
}