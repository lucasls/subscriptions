package com.github.lucasls.subscriptions.boundary.dto

data class Product(
    val code: String,
    val name: String,
    val price: Price,
    val subscriptionPeriodMonths: Int
)
