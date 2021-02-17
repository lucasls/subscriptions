package com.github.lucasls.subscriptions.boundary.dto

import com.github.lucasls.subscriptions.domain.value.SubscriptionStatus
import java.time.OffsetDateTime

data class Subscription(
    val productSnapshot: Product,
    val createdAt: OffsetDateTime,
    val expiresAt: OffsetDateTime,
    val status: SubscriptionStatus
)