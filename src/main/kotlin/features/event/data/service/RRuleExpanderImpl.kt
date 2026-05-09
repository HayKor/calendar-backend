package com.haykor.features.event.data.service

import com.haykor.core.util.mapper.toKotlinLocalDate
import com.haykor.features.event.domain.model.Event
import com.haykor.features.event.domain.model.EventException
import com.haykor.features.event.domain.model.EventOccurrence
import com.haykor.features.event.domain.model.mapper.toOccurrence
import com.haykor.features.event.domain.service.RRuleExpander
import kotlinx.datetime.*
import java.time.OffsetDateTime

class RRuleExpanderImpl : RRuleExpander {
    private enum class Freq { DAILY, WEEKLY, MONTHLY, YEARLY }

    private data class RRuleParams(
        val freq: Freq,
        val interval: Int = 1,
        val until: LocalDate? = null,
        val count: Int? = null,
        val byDay: List<DayOfWeek> = emptyList(),
    )

    override fun expand(
        event: Event,
        from: OffsetDateTime,
        to: OffsetDateTime,
        exceptions: List<EventException>,
    ): List<EventOccurrence> {
        val rrule = event.rrule ?: return emptyList()
        val params = parseRRule(rrule)
        val exceptionsByDate = exceptions.associateBy { it.originalDate }

        val fromDate = from.toKotlinLocalDate()
        val toDate = to.toKotlinLocalDate()

        return generateDates(event.startAt.toKotlinLocalDate(), params, toDate)
            .filter { it >= fromDate }
            .map { date ->
                event.toOccurrence(date, exceptionsByDate[date])
            }
    }

    private fun generateDates(start: LocalDate, params: RRuleParams, to: LocalDate): List<LocalDate> = when (params.freq) {
        Freq.DAILY -> generateSimple(start, params, to) { it.plus(params.interval, DateTimeUnit.DAY) }
        Freq.MONTHLY -> generateSimple(start, params, to) { it.plus(params.interval, DateTimeUnit.MONTH) }
        Freq.YEARLY -> generateSimple(start, params, to) { it.plus(params.interval, DateTimeUnit.YEAR) }
        Freq.WEEKLY -> generateWeekly(start, params, to)
    }

    private fun generateSimple(
        start: LocalDate,
        params: RRuleParams,
        to: LocalDate,
        next: (LocalDate) -> LocalDate,
    ): List<LocalDate> {
        val dates = mutableListOf<LocalDate>()
        var current = start
        while (current <= to) {
            if (params.until != null && current > params.until) break
            if (params.count != null && dates.size >= params.count) break
            dates.add(current)
            current = next(current)
        }
        return dates
    }

    private fun generateWeekly(start: LocalDate, params: RRuleParams, to: LocalDate): List<LocalDate> {
        val dates = mutableListOf<LocalDate>()
        val activeDays = params.byDay.ifEmpty { listOf(start.dayOfWeek) }
        var weekStart = start.previousOrSame(DayOfWeek.MONDAY)

        while (true) {
            for (day in activeDays.sortedBy { it.isoDayNumber }) {
                val candidate = weekStart.nextOrSame(day)
                if (candidate < start) continue
                if (candidate > to) return dates
                if (params.until != null && candidate > params.until) return dates
                if (params.count != null && dates.size >= params.count) return dates
                dates.add(candidate)
            }
            weekStart = weekStart.plus(params.interval, DateTimeUnit.WEEK)
        }
    }

    private fun parseRRule(rrule: String): RRuleParams {
        val parts = rrule.uppercase().split(";").associate { part ->
            val (key, value) = part.split("=", limit = 2)
            key.trim() to value.trim()
        }
        return RRuleParams(
            freq = when (parts["FREQ"]) {
                "DAILY" -> Freq.DAILY
                "WEEKLY" -> Freq.WEEKLY
                "MONTHLY" -> Freq.MONTHLY
                "YEARLY" -> Freq.YEARLY
                else -> throw IllegalArgumentException("Unsupported FREQ: ${parts["FREQ"]}")
            },
            interval = parts["INTERVAL"]?.toInt() ?: 1,
            until = parts["UNTIL"]?.let { parseUntilDate(it) },
            count = parts["COUNT"]?.toInt(),
            byDay = parts["BYDAY"]?.split(",")?.mapNotNull { parseDayOfWeek(it) } ?: emptyList(),
        )
    }

    private fun parseUntilDate(until: String): LocalDate {
        val d = until.take(8)
        return LocalDate(
            year = d.substring(0, 4).toInt(),
            month = d.substring(4, 6).toInt(),
            day = d.substring(6, 8).toInt(),
        )
    }

    private fun parseDayOfWeek(day: String): DayOfWeek? = when (day.takeLast(2)) {
        "MO" -> DayOfWeek.MONDAY
        "TU" -> DayOfWeek.TUESDAY
        "WE" -> DayOfWeek.WEDNESDAY
        "TH" -> DayOfWeek.THURSDAY
        "FR" -> DayOfWeek.FRIDAY
        "SA" -> DayOfWeek.SATURDAY
        "SU" -> DayOfWeek.SUNDAY
        else -> null
    }

    private fun LocalDate.previousOrSame(target: DayOfWeek): LocalDate {
        val diff = (dayOfWeek.isoDayNumber - target.isoDayNumber + 7) % 7
        return minus(diff, DateTimeUnit.DAY)
    }

    private fun LocalDate.nextOrSame(target: DayOfWeek): LocalDate {
        val diff = (target.isoDayNumber - dayOfWeek.isoDayNumber + 7) % 7
        return plus(diff, DateTimeUnit.DAY)
    }
}
