package com.github.lucasls.subscriptions.persistence.jpa.entity

import java.time.OffsetDateTime
import javax.persistence.Embeddable

@Embeddable
data class StatusChange(
    var changedAt: OffsetDateTime,
    var status: String,
)
