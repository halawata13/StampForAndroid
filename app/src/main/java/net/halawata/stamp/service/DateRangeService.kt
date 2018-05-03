package net.halawata.stamp.service

import android.app.Activity
import android.content.Context
import net.halawata.stamp.data.DateRange
import java.util.*

class DateRangeService(private val activity: Activity) {

    /**
     * 日付範囲を保存する
     */
    fun save(rangeName: String) {
        val editor = activity.getPreferences(Context.MODE_PRIVATE).edit()
        editor.putString(DateRangeService.dateRangeKey, rangeName)
        editor.apply()
    }

    /**
     * 日付範囲を保存する
     */
    fun save(dateRange: DateRange) {
        val editor = activity.getPreferences(Context.MODE_PRIVATE).edit()
        editor.putString(DateRangeService.dateRangeKey, dateRange.rangeName)

        if (dateRange.rangeName == Range.OTHERS.rangeName) {
            editor.putLong(DateRangeService.othersFromKey, dateRange.from.time)
            editor.putLong(DateRangeService.othersToKey, dateRange.to.time)
        }

        editor.apply()
    }

    /**
     * 日付範囲を読み込む
     */
    fun load(): DateRange {
        val preference = activity.getPreferences(Context.MODE_PRIVATE)
        val rangeName = preference.getString(DateRangeService.dateRangeKey, Range.RECENT_MONTH.rangeName)

        return create(rangeName)
    }

    /**
     * 日付範囲を生成する
     */
    private fun create(rangeName: String): DateRange {
        val preference = activity.getPreferences(Context.MODE_PRIVATE)
        var from = Date()
        var to = Date()

        when (rangeName) {
            Range.TODAY.rangeName -> {
                // today is the day
            }
            Range.RECENT_WEEK.rangeName -> {
                from = Date(System.currentTimeMillis() - 60 * 60 * 24 * 6 * 1000L)
            }
            Range.RECENT_MONTH.rangeName -> {
                from = Date(System.currentTimeMillis() - 60 * 60 * 24 * 29 * 1000L)
            }
            Range.RECENT_YEAR.rangeName -> {
                from = Date(System.currentTimeMillis() - 60 * 60 * 24 * 364 * 1000L)
            }
            else -> {
                // 期間の任意指定のデフォルトは30日間とする
                from = Date(preference.getLong(DateRangeService.othersFromKey, System.currentTimeMillis() - 60 * 60 * 24 * 29 * 1000L))
                to = Date(preference.getLong(DateRangeService.othersToKey, System.currentTimeMillis()))
            }
        }

        return DateRange(from, to, rangeName)
    }

    companion object {
        private const val dateRangeKey = "dateRangeKey"
        private const val othersFromKey = "othersFromKey"
        private const val othersToKey = "othersToKey"

        val labels = arrayOf(
                Range.TODAY.rangeName,
                Range.RECENT_WEEK.rangeName,
                Range.RECENT_MONTH.rangeName,
                Range.RECENT_YEAR.rangeName,
                Range.OTHERS.rangeName
        )
    }

    enum class Range(val rangeName: String) {
        TODAY("今日"),
        RECENT_WEEK("最近1週間"),
        RECENT_MONTH("最近30日"),
        RECENT_YEAR("最近1年間"),
        OTHERS("期間を指定"),
    }
}
