package com.github.lucasls.subscriptions.persistence.jpa.entity

import com.github.lucasls.subscriptions.domain.value.SubscriptionStatus
import java.time.OffsetDateTime
import javax.persistence.Embeddable

@Embeddable
data class StatusChange(
    var changedAt: OffsetDateTime,
    var status: SubscriptionStatus,
)