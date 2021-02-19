package com.github.lucasls.subscriptions.domain.subscription

import java.time.Duration
import java.time.OffsetDateTime

data class StatusPeriod(
    val startedAt: OffsetDateTime,
    val endedAt: OffsetDateTime,
    val status: SubscriptionStatus,
) {
    val duration: Duration = Duration.between(startedAt, endedAt)
}
