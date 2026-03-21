package com.haykor.features.auth.presentation

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GoogleUserDTO(
    val id: String,
    val email: String,
    @SerialName("verified_email") val verifiedEmail: Boolean,
    val name: String,
    @SerialName("given_name") val firstName: String,
    @SerialName("family_name") val lastName: String,
    val picture: String,
    val locale: String
)