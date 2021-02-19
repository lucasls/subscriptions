package com.github.lucasls.subscriptions.domain.subscription

import java.time.OffsetDateTime

data class StatusChange(
    val changedAt: OffsetDateTime,
    val status: SubscriptionStatus,
)
