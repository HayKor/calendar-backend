package com.haykor.features.event.data.repository

import com.haykor.features.event.data.local.EventExceptionTable
import com.haykor.features.event.domain.model.EventException
import com.haykor.features.event.domain.repository.CreateEventExceptionParams
import com.haykor.features.event.domain.repository.EventExceptionRepository
import com.haykor.features.event.domain.repository.UpdateEventExceptionParams
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.flow.singleOrNull
import kotlinx.coroutines.flow.toList
import kotlinx.datetime.LocalDate
import org.jetbrains.exposed.v1.core.*
import org.jetbrains.exposed.v1.r2dbc.*
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import java.time.OffsetDateTime

class EventExceptionRepositoryImpl(
    private val db: R2dbcDatabase,
) : EventExceptionRepository {

    override suspend fun create(params: CreateEventExceptionParams): EventException = suspendTransaction(db) {
        EventExceptionTable.insertReturning {
            it[event] = params.eventId
            it[originalDate] = params.originalDate
            it[isDeleted] = params.isDeleted
            it[titleOverride] = params.titleOverride
            it[startAtOverride] = params.startAtOverride
            it[endAtOverride] = params.endAtOverride
            it[locationOverride] = params.locationOverride
        }.single().toEventException()
    }

    override suspend fun getByEventIdAndDate(
        eventId: Int,
        date: LocalDate,
    ): EventException? = suspendTransaction(db) {
        EventExceptionTable
            .selectAll()
            .where { (EventExceptionTable.event eq eventId) and (EventExceptionTable.originalDate eq date) }
            .map { it.toEventException() }
            .singleOrNull()
    }

    override suspend fun getAllByEventId(eventId: Int): List<EventException> = suspendTransaction(db) {
        EventExceptionTable
            .selectAll()
            .where { EventExceptionTable.event eq eventId }
            .map { it.toEventException() }
            .toList()
    }

    override suspend fun getByEventIdsWithRange(
        eventIds: List<Int>,
        from: OffsetDateTime,
        to: OffsetDateTime,
    ): List<EventException> = suspendTransaction(db) {
        if (eventIds.isEmpty()) return@suspendTransaction emptyList()
        EventExceptionTable
            .selectAll()
            .where {
                (EventExceptionTable.event inList eventIds) and
                    (
                        // include overrides that fall within the range
                        EventExceptionTable.startAtOverride.isNull() or
                            (
                                (EventExceptionTable.startAtOverride greaterEq from) and
                                    (EventExceptionTable.startAtOverride lessEq to)
                                )
                        )
            }
            .map { it.toEventException() }
            .toList()
    }

    override suspend fun update(
        eventId: Int,
        originalDate: LocalDate,
        params: UpdateEventExceptionParams,
    ): EventException? = suspendTransaction(db) {
        EventExceptionTable.updateReturning(
            where = {
                (EventExceptionTable.event eq eventId) and
                    (EventExceptionTable.originalDate eq originalDate)
            },
        ) {
            params.isDeleted?.let { v -> it[isDeleted] = v }
            params.titleOverride?.let { v -> it[titleOverride] = v }
            params.startAtOverride?.let { v -> it[startAtOverride] = v }
            params.endAtOverride?.let { v -> it[endAtOverride] = v }
            params.locationOverride?.let { v -> it[locationOverride] = v }
        }.singleOrNull()?.toEventException()
    }

    override suspend fun delete(eventId: Int, originalDate: LocalDate): Boolean = suspendTransaction(db) {
        EventExceptionTable.deleteWhere {
            (EventExceptionTable.event eq eventId) and
                (EventExceptionTable.originalDate eq originalDate)
        } > 0
    }

    private fun ResultRow.toEventException() = EventException(
        id = this[EventExceptionTable.id].value,
        eventId = this[EventExceptionTable.event].value,
        originalDate = this[EventExceptionTable.originalDate],
        isDeleted = this[EventExceptionTable.isDeleted],
        titleOverride = this[EventExceptionTable.titleOverride],
        startAtOverride = this[EventExceptionTable.startAtOverride],
        endAtOverride = this[EventExceptionTable.endAtOverride],
        locationOverride = this[EventExceptionTable.locationOverride],
        createdAt = this[EventExceptionTable.createdAt],
    )
}
