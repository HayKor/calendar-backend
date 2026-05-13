package com.haykor.features.event.domain.service

import com.haykor.features.event.domain.model.EventError
import com.haykor.features.event.domain.model.RRuleInput
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.number

object RRuleBuilder {
    fun build(params: RRuleInput): String? {
        if (!params.isRecurring) return null
        val freq = params.freq ?: throw EventError.MissingFreq()

        return buildList {
            add("FREQ=${freq.name}")
            params.interval?.let { add("INTERVAL=$it") }
            params.count?.let { add("COUNT=$it") }
            params.until?.let { add("UNTIL=${formatUntil(it)}") }
            params.byDay.takeIf { it.isNotEmpty() }
                ?.let { add("BYDAY=${it.joinToString(",") { day -> day.toRRuleCode() }}") }
        }.joinToString(";")
    }

    private fun formatUntil(date: LocalDate): String {
        val year = date.year
        val month = date.month.number.toString().padStart(2, '0')
        val day = date.day.toString().padStart(2, '0')
        return "${year}${month}${day}T235959Z"
    }

    private fun DayOfWeek.toRRuleCode() = when (this) {
        DayOfWeek.MONDAY -> "MO"
        DayOfWeek.TUESDAY -> "TU"
        DayOfWeek.WEDNESDAY -> "WE"
        DayOfWeek.THURSDAY -> "TH"
        DayOfWeek.FRIDAY -> "FR"
        DayOfWeek.SATURDAY -> "SA"
        DayOfWeek.SUNDAY -> "SU"
    }
}
