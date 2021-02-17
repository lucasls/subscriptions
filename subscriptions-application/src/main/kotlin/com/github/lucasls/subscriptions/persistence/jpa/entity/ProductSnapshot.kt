package com.github.lucasls.subscriptions.persistence.jpa.entity

import javax.persistence.AttributeOverride
import javax.persistence.AttributeOverrides
import javax.persistence.Column
import javax.persistence.Embeddable
import javax.persistence.Embedded

@Embeddable
data class ProductSnapshot(
    var code: String,
    var name: String,

    @Embedded
    @AttributeOverrides(
        AttributeOverride(name = "value", column = Column(name = "price_value")),
        AttributeOverride(name = "unit", column = Column(name = "price_unit")),
    )
    var price: Price,

    var subscriptionPeriodMonths: Int,
    var taxRate: Double
)
