package com.haykor.features.event.domain.repository

import com.haykor.features.event.domain.model.CreateEventParams
import com.haykor.features.event.domain.model.Event
import com.haykor.features.event.domain.model.UpdateEventParams
import java.time.OffsetDateTime

interface EventRepository {
    suspend fun create(params: CreateEventParams): Event
    suspend fun getById(id: Int): Event?
    suspend fun getAllByUserId(userId: Int): List<Event>
    suspend fun getByUserIdAndRange(
        userId: Int,
        from: OffsetDateTime,
        to: OffsetDateTime,
    ): List<Event>

    suspend fun update(id: Int, params: UpdateEventParams): Event?
    suspend fun delete(id: Int): Boolean
}
