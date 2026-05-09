package com.haykor.core.visibility.domain.service

import com.haykor.core.visibility.domain.model.Visibility

object VisibilityResolver {
    fun resolve(
        eventVisibility: Visibility,
        categoryVisibility: Visibility?, // nullable — event may have no category
        userGlobalVisibility: Visibility,
    ): Visibility = listOfNotNull(eventVisibility, categoryVisibility, userGlobalVisibility)
        .maxBy { it.ordinal } // highest ordinal = most restrictive wins
}
