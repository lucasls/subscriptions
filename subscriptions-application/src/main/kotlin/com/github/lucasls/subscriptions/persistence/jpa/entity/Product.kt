package com.github.lucasls.subscriptions.persistence.jpa.entity

import javax.persistence.AttributeOverride
import javax.persistence.AttributeOverrides
import javax.persistence.Column
import javax.persistence.Embedded
import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class Product(
    @Id
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
