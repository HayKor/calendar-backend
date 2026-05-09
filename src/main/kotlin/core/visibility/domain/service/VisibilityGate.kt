package com.haykor.core.visibility.domain.service

import com.haykor.core.visibility.domain.model.ViewerRelation
import com.haykor.core.visibility.domain.model.Visibility

object VisibilityGate {
    fun canView(
        relation: ViewerRelation,
        eventVisibility: Visibility,
        categoryVisibility: Visibility?,
        userGlobalVisibility: Visibility,
    ): Boolean {
        val effectiveVisibility = VisibilityResolver.resolve(
            eventVisibility = eventVisibility,
            categoryVisibility = categoryVisibility,
            userGlobalVisibility = userGlobalVisibility,
        )
        return VisibilityChecker.canView(effectiveVisibility, relation)
    }
}
