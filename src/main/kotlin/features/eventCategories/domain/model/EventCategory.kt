package com.haykor.features.eventCategories.domain.model

import com.haykor.core.visibility.domain.model.Visibility

data class EventCategory(
    val id: Int,
    val userId: Int,
    val name: String,
    val visibility: Visibility = Visibility.Public,
    val colorHex: String,
    val iconName: String? = null,
)
