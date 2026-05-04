package com.haykor.features.eventCategories.domain.model

import com.haykor.core.common.domain.Visibility

data class CreateEventCategoryParams(
    val userId: Int,
    val name: String,
    val visibility: Visibility = Visibility.Public,
    val colorHex: String,
    val iconName: String? = null,
)

data class EventCategory(
    val id: Int,
    val userId: Int,
    val name: String,
    val visibility: Visibility = Visibility.Public,
    val colorHex: String,
    val iconName: String? = null,
)
