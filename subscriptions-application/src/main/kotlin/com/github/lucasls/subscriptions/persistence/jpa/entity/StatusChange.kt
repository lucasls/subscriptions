package com.github.lucasls.subscriptions.persistence.jpa.entity

import com.github.lucasls.subscriptions.domain.value.SubscriptionStatus
import java.time.OffsetDateTime
import javax.persistence.Embeddable
import javax.persistence.EnumType
import javax.persistence.Enumerated

@Embeddable
data class StatusChange(
    var changedAt: OffsetDateTime,

    @Enumerated(EnumType.STRING)
    var status: SubscriptionStatus,
)