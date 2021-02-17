package com.github.lucasls.subscriptions.persistence.jpa.entity

import java.math.BigDecimal
import javax.persistence.Embeddable

@Embeddable
data class Price(
    var value: BigDecimal,
    var unit: String,
)
