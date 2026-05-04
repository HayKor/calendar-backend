package com.haykor.features.eventCategories.data.repository

import com.haykor.features.eventCategories.data.local.EventCategoriesTable
import com.haykor.features.eventCategories.domain.model.CreateEventCategoryParams
import com.haykor.features.eventCategories.domain.model.EventCategory
import com.haykor.features.eventCategories.domain.repository.EventCategoriesRepository
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.flow.singleOrNull
import kotlinx.coroutines.flow.toList
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.r2dbc.R2dbcDatabase
import org.jetbrains.exposed.v1.r2dbc.deleteWhere
import org.jetbrains.exposed.v1.r2dbc.insertReturning
import org.jetbrains.exposed.v1.r2dbc.selectAll
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction

class EventCategoriesRepositoryImpl(
    private val db: R2dbcDatabase,
) : EventCategoriesRepository {
    override suspend fun create(params: CreateEventCategoryParams): EventCategory = suspendTransaction(db) {
        EventCategoriesTable.insertReturning {
            it[name] = params.name
            it[user] = params.userId
            it[defaultVisibility] = params.visibility
            it[iconName] = params.iconName
            it[colorHex] = params.colorHex
        }.map { it.toEventCategory() }.single()
    }

    override suspend fun getById(id: Int): EventCategory? = suspendTransaction(db) {
        EventCategoriesTable
            .selectAll()
            .where { EventCategoriesTable.id eq id }
            .map { it.toEventCategory() }
            .singleOrNull()
    }

    override suspend fun getAllByUser(userId: Int): List<EventCategory> = suspendTransaction(db) {
        EventCategoriesTable
            .selectAll()
            .where { EventCategoriesTable.user eq userId }
            .map { it.toEventCategory() }
            .toList()
    }

    override suspend fun deleteById(id: Int): Boolean = suspendTransaction(db) {
        EventCategoriesTable
            .deleteWhere { EventCategoriesTable.id eq id } > 0
    }

    private fun ResultRow.toEventCategory() = EventCategory(
        id = this[EventCategoriesTable.id].value,
        userId = this[EventCategoriesTable.user].value,
        name = this[EventCategoriesTable.name],
        visibility = this[EventCategoriesTable.defaultVisibility],
        iconName = this[EventCategoriesTable.iconName],
        colorHex = this[EventCategoriesTable.colorHex],
    )
}