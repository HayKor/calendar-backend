package com.haykor.features.eventCategories.presentation.model

import com.haykor.core.common.domain.Visibility
import kotlinx.serialization.Serializable

@Serializable
data class CreateEventCategoryRequest(
    val name: String,
    val userId: Int,
    val visibility: Visibility,
    val colorHex: String,
    val iconName: String? = null,
)

@Serializable
data class EventCategoryResponse(
    val name: String,
    val userId: Int,
    val visibility: Visibility,
    val colorHex: String,
    val iconName: String? = null,
)
