package com.github.lucasls.subscriptions.domain.model

import com.github.lucasls.subscriptions.domain.value.SubscriptionStatus
import java.time.OffsetDateTime

data class StatusChange(
    val changedAt: OffsetDateTime,
    val status: SubscriptionStatus,
)
