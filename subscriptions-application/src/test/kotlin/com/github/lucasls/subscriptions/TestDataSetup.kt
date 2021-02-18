package com.github.lucasls.subscriptions

import com.github.lucasls.subscriptions.domain.model.Subscription
import com.github.lucasls.subscriptions.domain.product.ProductRepository
import com.github.lucasls.subscriptions.domain.subscription.SubscriptionRepository
import com.github.lucasls.subscriptions.persistence.jpa.ProductJpaRepository
import com.github.lucasls.subscriptions.persistence.jpa.entity.Price
import com.github.lucasls.subscriptions.persistence.jpa.entity.Product
import mu.KotlinLogging
import org.springframework.boot.test.context.TestConfiguration
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.time.Period
import java.util.UUID
import javax.annotation.PostConstruct

@TestConfiguration
class TestDataSetup(
    val productJpaRepository: ProductJpaRepository,
    val productRepository: ProductRepository,
    val subscriptionRepository: SubscriptionRepository,
) {
    private val logger = KotlinLogging.logger {}

    @PostConstruct
    fun onInit() {
        val products = listOf(
            Product(
                code = "ANNUAL",
                name = "Annual Payment",
                price = Price(
                    value = BigDecimal("83.99"),
                    unit = "EUR",
                ),
                subscriptionPeriodMonths = 12,
                taxRate = 0.07,
            ),
            Product(
                code = "SEMI_ANNUAL",
                name = "Semi-Annual Payment",
                price = Price(
                    value = BigDecimal("59.99"),
                    unit = "EUR",
                ),
                subscriptionPeriodMonths = 6,
                taxRate = 0.19,
            ),
            Product(
                code = "QUARTERLY",
                name = "Quarterly payment",
                price = Price(
                    value = BigDecimal("38.99"),
                    unit = "EUR",
                ),
                subscriptionPeriodMonths = 3,
                taxRate = 0.19,
            ),
        )

        productJpaRepository.saveAll(products)

        val annualProduct = productRepository.findByCode("ANNUAL")!!

        // Active subscription
        subscriptionRepository.create(
            userId = UUID.fromString("00000000-0000-0000-0000-000000000001"),
            subscription = Subscription(
                productSnapshot = annualProduct,
                createdAt = OffsetDateTime.now() - Period.ofMonths(6),
            )
        )
    }
}