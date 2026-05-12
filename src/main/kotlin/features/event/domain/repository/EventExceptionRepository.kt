package com.haykor.features.event.domain.repository

import com.haykor.features.event.domain.model.EventException
import kotlinx.datetime.LocalDate
import java.time.OffsetDateTime

interface EventExceptionRepository {
    suspend fun create(params: CreateEventExceptionParams): EventException
    suspend fun getByEventIdAndDate(eventId: Int, date: LocalDate): EventException?
    suspend fun getAllByEventId(eventId: Int): List<EventException>
    suspend fun getByEventIdsWithRange(eventIds: List<Int>, from: OffsetDateTime, to: OffsetDateTime): List<EventException>
    suspend fun update(eventId: Int, originalDate: LocalDate, params: UpdateEventExceptionParams): EventException?
    suspend fun delete(eventId: Int, originalDate: LocalDate): Boolean
}
