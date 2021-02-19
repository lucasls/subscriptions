package com.github.lucasls.subscriptions.domain.product

import io.kotest.matchers.shouldBe
import org.joda.money.Money
import org.junit.jupiter.api.Test
import java.time.Period

internal class ProductTest {
    private val subject = Product(
        code = "ANNUAL",
        name = "Annual Payment",
        price = Money.parse("EUR 83.99"),
        subscriptionPeriod = Period.ofMonths(12),
        taxRate = 0.19
    )

    @Test
    internal fun `should calculate tax correctly`() {
        subject.tax shouldBe Money.parse("EUR 15.95")
    }
}
