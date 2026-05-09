package com.haykor.core.common.presentation

import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(
    val error: String,
    val path: String,
    val status: Int,
)
