package com.github.lucasls.subscriptions.domain.model

import com.github.lucasls.subscriptions.domain.value.SubscriptionStatus
import java.time.Duration
import java.time.OffsetDateTime

data class StatusPeriod(
    val startedAt: OffsetDateTime,
    val endedAt: OffsetDateTime,
    val status: SubscriptionStatus,
) {
    val duration: Duration = Duration.between(startedAt, endedAt)
}
