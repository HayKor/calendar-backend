package com.haykor.features.event.domain.model

import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate

data class RRuleInput(
    val isRecurring: Boolean,
    val freq: Freq? = null,
    val interval: Int? = null,
    val until: LocalDate? = null,
    val count: Int? = null,
    val byDay: List<DayOfWeek> = emptyList(),
)

enum class Freq { DAILY, WEEKLY, MONTHLY, YEARLY }
