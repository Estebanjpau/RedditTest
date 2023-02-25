package com.example.reddittest

import android.view.View
import java.util.concurrent.TimeUnit
import com.example.reddittest.databinding.ItemPostBinding

private const val SECOND_MILLIS = 1
private const val MINUTE_MILLIS = 60 * SECOND_MILLIS
private const val HOURS_MILLIS = 60 * MINUTE_MILLIS
private const val DAYS_MILLIS = 24 * HOURS_MILLIS
private const val WEEKS_MILLIS = 7 * DAYS_MILLIS
private const val MONTHS_MILLIS = 4 * WEEKS_MILLIS
private const val YEARS_MILLIS = 12 * MONTHS_MILLIS

object PostUtils {

    fun getTimeAgo(time: Int): String {

        val now = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis())
        if (time > now || time <= 0) {
            return ""
        }
        val diff = now - time
        return when {
            diff < MINUTE_MILLIS -> "Ahora"
            diff < 60 * MINUTE_MILLIS -> "${diff / MINUTE_MILLIS}m"
            diff < 24 * HOURS_MILLIS -> "${diff / HOURS_MILLIS}h"
            diff < 7 * DAYS_MILLIS -> "${diff / DAYS_MILLIS}d"
            diff < 4 * WEEKS_MILLIS -> "${diff / WEEKS_MILLIS}s"
            diff < 12 * MONTHS_MILLIS -> "${diff / MONTHS_MILLIS}mes(es)"
            else -> "${diff / YEARS_MILLIS}a)"
        }
    }

    fun resumeCounterNumber(number: Int): String {

        var result = number.toString()
        if (number > 999) {
            result = if (number <= 999999) {
                String.format("%.1fK", number.toFloat() / 1000)
            } else {
                String.format("%.1fM", number.toFloat() / 1000000)
            }
        }
        return result
    }

}