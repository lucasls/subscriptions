package com.github.lucasls.subscriptions.domain.model

import org.joda.money.Money
import java.time.Period

data class Product(
    val code: String,
    val name: String,
    val price: Money,
    val subscriptionPeriod: Period
)
