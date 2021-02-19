package com.github.lucasls.subscriptions.boundary.dto

import java.time.OffsetDateTime
import java.util.UUID

data class Subscription(
    val id: UUID,
    val productSnapshot: Product,
    val createdAt: OffsetDateTime,
    val expiresAt: OffsetDateTime,
    val status: Status
) {
    enum class Status {
        ACTIVE, PAUSED, EXPIRED, CANCELED
    }
}