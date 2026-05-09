package com.haykor.core.visibility.domain.service

import com.haykor.core.visibility.domain.model.ViewerRelation
import com.haykor.core.visibility.domain.model.Visibility

object VisibilityChecker {
    fun canView(effectiveVisibility: Visibility, relation: ViewerRelation): Boolean = when (effectiveVisibility) {
        Visibility.Public -> true
        Visibility.Friends -> relation is ViewerRelation.Owner || relation is ViewerRelation.Friend
        Visibility.Private -> relation is ViewerRelation.Owner
    }
}
