package com.haykor.features.event.data.repository

import com.haykor.features.event.data.local.EventTable
import com.haykor.features.event.domain.model.Event
import com.haykor.features.event.domain.repository.CreateEventParams
import com.haykor.features.event.domain.repository.EventRepository
import com.haykor.features.event.domain.repository.UpdateEventParams
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.flow.singleOrNull
import kotlinx.coroutines.flow.toList
import org.jetbrains.exposed.v1.core.*
import org.jetbrains.exposed.v1.r2dbc.*
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction
import java.time.OffsetDateTime

class EventRepositoryImpl(
    private val db: R2dbcDatabase,
) : EventRepository {
    override suspend fun create(params: CreateEventParams): Event = suspendTransaction(db) {
        EventTable.insertReturning {
            it[user] = params.userId
            it[category] = params.categoryId
            it[title] = params.title
            it[description] = params.description
            it[location] = params.location
            it[eventTimezone] = params.eventTimezone
            it[startAt] = params.startAt
            it[endAt] = params.endAt
            it[isAllDay] = params.isAllDay
            it[visibility] = params.visibility
            it[isRecurring] = params.isRecurring
            it[rrule] = params.rrule
            it[recurrenceUntil] = params.recurrenceUntil
        }.single().toEvent()
    }

    override suspend fun getById(id: Int): Event? = suspendTransaction(db) {
        EventTable
            .selectAll()
            .where { EventTable.id eq id }
            .singleOrNull()
            ?.toEvent()
    }

    override suspend fun getAllByUserId(userId: Int): List<Event> = suspendTransaction(db) {
        EventTable
            .selectAll()
            .where { EventTable.user eq userId }
            .map { it.toEvent() }
            .toList()
    }

    /**
     * Get events by user id and range. Returns simple events & recurring events
     *
     * @param userId [Int]
     * @param from [OffsetDateTime]
     * @param to [OffsetDateTime]
     * @return list of [Event]
     */
    override suspend fun getByUserIdAndRange(
        userId: Int,
        from: OffsetDateTime,
        to: OffsetDateTime,
    ): List<Event> = suspendTransaction(db) {
        EventTable
            .selectAll()
            .where {
                (EventTable.user eq userId) and
                    (
                        // Обычные ивенты в диапазоне
                        (
                            (EventTable.isRecurring eq false) and
                                (EventTable.startAt lessEq to) and
                                (EventTable.startAt greaterEq from)
                            ) or
                            // Recurring: началось до конца диапазона и не закончилось до начала
                            (
                                (EventTable.isRecurring eq true) and
                                    (EventTable.startAt lessEq to) and
                                    (
                                        EventTable.recurrenceUntil.isNull() or
                                            (EventTable.recurrenceUntil greaterEq from)
                                        )
                                )
                        )
            }
            .map { it.toEvent() }
            .toList()
    }

    override suspend fun update(id: Int, params: UpdateEventParams): Event? = suspendTransaction(db) {
        EventTable.updateReturning(
            where = { EventTable.id eq id },
        ) {
            params.categoryId?.let { v -> it[category] = v }
            params.title?.let { v -> it[title] = v }
            params.description?.let { v -> it[description] = v }
            params.location?.let { v -> it[location] = v }
            params.eventTimezone?.let { v -> it[eventTimezone] = v }
            params.startAt?.let { v -> it[startAt] = v }
            params.endAt?.let { v -> it[endAt] = v }
            params.isAllDay?.let { v -> it[isAllDay] = v }
            params.visibility?.let { v -> it[visibility] = v }
            params.isRecurring?.let { v -> it[isRecurring] = v }
            params.rrule?.let { v -> it[rrule] = v }
            params.recurrenceUntil?.let { v -> it[recurrenceUntil] = v }
            it[updatedAt] = OffsetDateTime.now()
        }.singleOrNull()?.toEvent()
    }

    override suspend fun delete(id: Int): Boolean = suspendTransaction(db) {
        EventTable.deleteWhere { EventTable.id eq id } > 0
    }

    private fun ResultRow.toEvent() = Event(
        id = this[EventTable.id].value,
        userId = this[EventTable.user].value,
        categoryId = this[EventTable.category]?.value,
        title = this[EventTable.title],
        description = this[EventTable.description],
        location = this[EventTable.location],
        eventTimezone = this[EventTable.eventTimezone],
        startAt = this[EventTable.startAt],
        endAt = this[EventTable.endAt],
        isAllDay = this[EventTable.isAllDay],
        visibility = this[EventTable.visibility],
        isRecurring = this[EventTable.isRecurring],
        rrule = this[EventTable.rrule],
        recurrenceUntil = this[EventTable.recurrenceUntil],
        createdAt = this[EventTable.createdAt],
        updatedAt = this[EventTable.updatedAt],
    )
}
