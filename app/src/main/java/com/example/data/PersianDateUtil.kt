package com.example.data

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Robust Solar Hijri (Jalali / Shamsi) calendar conversion and formatting utilities.
 */
object PersianDateUtil {

    private val PERSIAN_MONTH_NAMES = arrayOf(
        "فروردین", "اردیبهشت", "خرداد", "تیر", "مرداد", "شهریور",
        "مهر", "آبان", "آذر", "دی", "بهمن", "اسفند"
    )

    private val PERSIAN_WEEKDAYS = arrayOf(
        "شنبه", "یکشنبه", "دوشنبه", "سه‌شنبه", "چهارشنبه", "پنج‌شنبه", "جمعه"
    )

    data class PersianDate(
        val year: Int,
        val month: Int, // 1..12
        val day: Int    // 1..31
    ) {
        val monthName: String
            get() = if (month in 1..12) PERSIAN_MONTH_NAMES[month - 1] else ""

        fun formatLong(): String {
            return "${toPersianDigits(day)} $monthName ${toPersianDigits(year)}"
        }

        fun formatShort(): String {
            return "$year/${month.toString().padStart(2, '0')}/${day.toString().padStart(2, '0')}"
        }
    }

    /**
     * Default program start date as requested: 28 Shahrivar 1405.
     * In Gregorian: 2026-09-19.
     */
    val DEFAULT_START_PERSIAN = PersianDate(1405, 6, 28)
    const val DEFAULT_START_GREGORIAN = "2026-09-19"

    /**
     * Converts a Gregorian date (year, month 1..12, day 1..31) to Jalali (Solar Hijri).
     */
    fun gregorianToJalali(gy: Int, gm: Int, gd: Int): PersianDate {
        val gDaysInMonth = intArrayOf(0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)
        val jDaysInMonth = intArrayOf(0, 31, 31, 31, 31, 31, 31, 30, 30, 30, 30, 30, 29)

        val gy2 = if (gm > 2) gy + 1 else gy
        var gDays = 355666 + (365 * gy) + ((gy2 + 3) / 4) - ((gy2 + 99) / 100) + ((gy2 + 399) / 400) + gd
        for (i in 1 until gm) {
            gDays += gDaysInMonth[i]
        }

        var jDays = gDays - 355668
        val jNp = jDays / 12053
        jDays %= 12053

        var jy = 979 + 33 * jNp + 4 * (jDays / 1461)
        jDays %= 1461

        if (jDays >= 366) {
            jy += (jDays - 1) / 365
            jDays = (jDays - 1) % 365
        }

        var jm = 0
        var jd = 0
        for (i in 1..12) {
            if (jDays < jDaysInMonth[i]) {
                jm = i
                jd = jDays + 1
                break
            }
            jDays -= jDaysInMonth[i]
        }

        return PersianDate(jy, jm, jd)
    }

    /**
     * Converts a Jalali date (year, month 1..12, day 1..31) to Gregorian.
     */
    fun jalaliToGregorian(jy: Int, jm: Int, jd: Int): Triple<Int, Int, Int> {
        val jDaysInMonth = intArrayOf(0, 31, 31, 31, 31, 31, 31, 30, 30, 30, 30, 30, 29)
        val gDaysInMonth = intArrayOf(0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31)

        val jNp = (jy - 979) / 33
        val jRem = (jy - 979) % 33

        var jDays = 355668 + (12053 * jNp) + (1461 * (jRem / 4)) + (365 * (jRem % 4))
        for (i in 1 until jm) {
            jDays += jDaysInMonth[i]
        }
        jDays += (jd - 1)

        var gDays = jDays
        var gy = 400 * (gDays / 146097)
        gDays %= 146097

        if (gDays >= 36525) {
            gDays--
            gy += 100 * (gDays / 36524)
            gDays %= 36524
            if (gDays >= 365) gDays++
        }

        gy += 4 * (gDays / 1461)
        gDays %= 1461

        if (gDays >= 366) {
            gDays--
            gy += gDays / 365
            gDays %= 365
        }

        val leap = (gy % 4 == 0 && gy % 100 != 0) || (gy % 400 == 0)
        var gm = 0
        var gd = 0

        for (i in 1..12) {
            var mDays = gDaysInMonth[i]
            if (i == 2 && leap) mDays = 29
            if (gDays < mDays) {
                gm = i
                gd = gDays + 1
                break
            }
            gDays -= mDays
        }

        return Triple(gy, gm, gd)
    }

    /**
     * Converts date string "YYYY-MM-DD" to formatted Persian date string e.g. "۲۸ شهریور ۱۴۰۵"
     */
    fun toPersianDisplayDate(isoDate: String): String {
        return try {
            val parts = isoDate.split("-")
            if (parts.size == 3) {
                val p = gregorianToJalali(parts[0].toInt(), parts[1].toInt(), parts[2].toInt())
                p.formatLong()
            } else {
                isoDate
            }
        } catch (e: Exception) {
            isoDate
        }
    }

    /**
     * Converts current timestamp millis to formatted Persian date
     */
    fun toPersianDisplayDate(timestamp: Long): String {
        val cal = Calendar.getInstance()
        cal.timeInMillis = timestamp
        val gy = cal.get(Calendar.YEAR)
        val gm = cal.get(Calendar.MONTH) + 1
        val gd = cal.get(Calendar.DAY_OF_MONTH)
        val p = gregorianToJalali(gy, gm, gd)
        return p.formatLong()
    }

    /**
     * Returns weekday name in Persian for given ISO Gregorian date (e.g. شنبه, یکشنبه)
     */
    fun getPersianWeekdayName(isoDate: String): String {
        return try {
            val parts = isoDate.split("-")
            val cal = Calendar.getInstance()
            cal.set(parts[0].toInt(), parts[1].toInt() - 1, parts[2].toInt())
            when (cal.get(Calendar.DAY_OF_WEEK)) {
                Calendar.SATURDAY -> "شنبه"
                Calendar.SUNDAY -> "یکشنبه"
                Calendar.MONDAY -> "دوشنبه"
                Calendar.TUESDAY -> "سه‌شنبه"
                Calendar.WEDNESDAY -> "چهارشنبه"
                Calendar.THURSDAY -> "پنج‌شنبه"
                Calendar.FRIDAY -> "جمعه"
                else -> ""
            }
        } catch (e: Exception) {
            ""
        }
    }

    /**
     * Converts English digits 0-9 to Persian digits ۰-۹
     */
    fun toPersianDigits(text: Any?): String {
        if (text == null) return ""
        val str = text.toString()
        val persianDigits = charArrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')
        val sb = StringBuilder()
        for (ch in str) {
            if (ch in '0'..'9') {
                sb.append(persianDigits[ch - '0'])
            } else {
                sb.append(ch)
            }
        }
        return sb.toString()
    }

    /**
     * Formats seconds into MM:SS with Persian digits
     */
    fun formatTimer(seconds: Int): String {
        val m = (seconds / 60).toString().padStart(2, '0')
        val s = (seconds % 60).toString().padStart(2, '0')
        return "${toPersianDigits(m)}:${toPersianDigits(s)}"
    }

    /**
     * Today's ISO date (YYYY-MM-DD)
     */
    fun todayIso(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        return sdf.format(Date())
    }
}
