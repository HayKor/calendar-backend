package com.haykor.core.util.mapper

import kotlinx.datetime.LocalDate
import java.time.OffsetDateTime

fun OffsetDateTime.toKotlinLocalDate() = LocalDate(year, monthValue, dayOfMonth)
