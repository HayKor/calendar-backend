package com.haykor.features.user.domain.model

import com.haykor.core.exception.BadRequest

enum class PendingRequestType {
    Incoming,
    Outgoing,
    ;

    companion object {
        fun from(value: String?): PendingRequestType? = when (value?.lowercase()) {
            "incoming" -> Incoming

            "outgoing" -> Outgoing

            null -> null

            // no filter — return all
            else -> throw BadRequest("Invalid type, expected 'incoming' or 'outgoing'")
        }
    }
}
