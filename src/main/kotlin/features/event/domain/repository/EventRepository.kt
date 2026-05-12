package com.haykor.features.event.domain.repository

import com.haykor.features.event.data.model.CreateEventDbParams
import com.haykor.features.event.data.model.UpdateEventDbParams
import com.haykor.features.event.domain.model.Event
import java.time.OffsetDateTime

interface EventRepository {
    suspend fun create(params: CreateEventDbParams): Event
    suspend fun getById(id: Int): Event?
    suspend fun getAllByUserId(userId: Int): List<Event>
    suspend fun getByUserIdAndRange(
        userId: Int,
        from: OffsetDateTime,
        to: OffsetDateTime,
    ): List<Event>

    suspend fun update(id: Int, params: UpdateEventDbParams): Event?
    suspend fun delete(id: Int): Boolean
}
