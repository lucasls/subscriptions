package com.github.lucasls.subscriptions.persistence

import com.github.lucasls.subscriptions.domain.model.Product
import com.github.lucasls.subscriptions.domain.product.ProductRepository
import org.joda.money.Money
import org.springframework.stereotype.Component
import java.time.Period

@Component
class InMemoryProductRepository : ProductRepository {
    override fun listAll(): List<Product> = listOf(
        Product(
            code = "ANNUAL",
            name = "Annual Payment",
            price = Money.parse("EUR 83.99"),
            subscriptionPeriod = Period.ofMonths(12),
            taxRate = 0.07,
        ),
        Product(
            code = "SEMI_ANNUAL",
            name = "Semi-Annual Payment",
            price = Money.parse("EUR 59.99"),
            subscriptionPeriod = Period.ofMonths(6),
            taxRate = 0.19,
        ),
        Product(
            code = "QUARTERLY",
            name = "Quarterly payment",
            price = Money.parse("EUR 38.99"),
            subscriptionPeriod = Period.ofMonths(3),
            taxRate = 0.19,
        ),
    )

    override fun findByCode(code: String): Product? {
        return listAll().firstOrNull {
            it.code == code
        }
    }
}