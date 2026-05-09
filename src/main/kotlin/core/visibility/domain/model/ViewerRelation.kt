package com.haykor.core.visibility.domain.model

sealed class ViewerRelation {
    data object Owner : ViewerRelation()
    data object Friend : ViewerRelation()
    data object Stranger : ViewerRelation()
}
