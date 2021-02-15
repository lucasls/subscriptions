package com.github.lucasls.subscriptions.persistence

import com.github.lucasls.subscriptions.domain.model.Product
import com.github.lucasls.subscriptions.domain.repository.ProductRepository
import org.joda.money.Money
import org.springframework.stereotype.Component
import java.time.Period

@Component
class ProductRepositoryImpl : ProductRepository {
    override fun listAll(): List<Product> = listOf(
        Product(
            code = "ANNUAL",
            name = "Annual Payment",
            price = Money.parse("EUR 83.99"),
            subscriptionPeriod = Period.ofMonths(12),
        ),
        Product(
            code = "SEMI_ANNUAL",
            name = "Semi-Annual Payment",
            price = Money.parse("EUR 59.99"),
            subscriptionPeriod = Period.ofMonths(6),
        ),
        Product(
            code = "QUARTERLY",
            name = "Quarterly payment",
            price = Money.parse("EUR 38.99"),
            subscriptionPeriod = Period.ofMonths(3),
        ),
    )
}