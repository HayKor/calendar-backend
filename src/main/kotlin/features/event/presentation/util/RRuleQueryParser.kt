package com.haykor.features.event.presentation.util

import com.haykor.core.exception.BadRequest
import com.haykor.features.event.domain.model.Freq
import com.haykor.features.event.domain.model.RRuleInput
import io.ktor.server.routing.*
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate

fun RoutingCall.parseRRuleInput(): RRuleInput {
    val params = request.queryParameters
    val freq = params["freq"]?.uppercase()?.let {
        runCatching { Freq.valueOf(it) }.getOrElse { throw BadRequest("Invalid freq: $it") }
    }
    return RRuleInput(
        isRecurring = freq != null,
        freq = freq,
        interval = params["interval"]?.toIntOrNull(),
        until = params["until"]?.let {
            runCatching { LocalDate.parse(it) }.getOrElse { throw BadRequest("Invalid until date, expected YYYY-MM-DD") }
        },
        count = params["count"]?.toIntOrNull(),
        byDay = params["byDay"]?.split(",")?.mapNotNull { parseDayOfWeek(it.trim()) } ?: emptyList(),
    )
}

private fun parseDayOfWeek(value: String): DayOfWeek? = when (value.uppercase()) {
    "MO" -> DayOfWeek.MONDAY
    "TU" -> DayOfWeek.TUESDAY
    "WE" -> DayOfWeek.WEDNESDAY
    "TH" -> DayOfWeek.THURSDAY
    "FR" -> DayOfWeek.FRIDAY
    "SA" -> DayOfWeek.SATURDAY
    "SU" -> DayOfWeek.SUNDAY
    else -> null
}
