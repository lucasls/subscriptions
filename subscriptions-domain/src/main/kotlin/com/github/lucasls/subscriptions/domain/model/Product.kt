package com.github.lucasls.subscriptions.domain.model

import org.joda.money.Money
import java.math.RoundingMode
import java.time.Period

data class Product(
    val code: String,
    val name: String,
    val price: Money,
    val subscriptionPeriod: Period,
    val taxRate: Double
) {
    val tax: Money = price.multipliedBy(taxRate, RoundingMode.FLOOR)
}
