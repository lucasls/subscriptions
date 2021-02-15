package com.github.lucasls.subscriptions.domain.model

import java.time.OffsetDateTime
import java.util.UUID

data class Subscription(
    val id: UUID,
    val productSnapshot: Product,
    val createdAt: OffsetDateTime
) {
    val expiresAt: OffsetDateTime
        get() = createdAt + productSnapshot.subscriptionPeriod
}
