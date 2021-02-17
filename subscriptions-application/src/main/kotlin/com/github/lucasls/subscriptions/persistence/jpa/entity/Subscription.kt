package com.github.lucasls.subscriptions.persistence.jpa.entity

import java.time.OffsetDateTime
import java.util.UUID
import javax.persistence.AttributeOverride
import javax.persistence.AttributeOverrides
import javax.persistence.Column
import javax.persistence.ElementCollection
import javax.persistence.Embedded
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.Id
import javax.persistence.OrderBy

@Entity
data class Subscription(
    @Id
    var id: UUID,

    var userId: UUID,

    @Embedded
    @AttributeOverrides(
        AttributeOverride(name = "code", column = Column(name = "product_code")),
        AttributeOverride(name = "name", column = Column(name = "product_name")),
    )
    var productSnapshot: ProductSnapshot,

    var createdAt: OffsetDateTime,

    @ElementCollection(fetch = FetchType.EAGER)
    @OrderBy("changedAt")
    var statusChanges: MutableList<StatusChange>
)
