package com.github.lucasls.subscriptions.boundary.dto

import com.github.lucasls.subscriptions.domain.value.SubscriptionStatus
import java.time.OffsetDateTime
import java.util.UUID

data class Subscription(
    val id: UUID,
    val productSnapshot: Product,
    val createdAt: OffsetDateTime,
    val expiresAt: OffsetDateTime,
    val status: SubscriptionStatus
)