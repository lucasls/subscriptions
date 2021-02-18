package com.github.lucasls.subscriptions

import com.github.lucasls.subscriptions.domain.model.StatusChange
import com.github.lucasls.subscriptions.domain.model.Subscription
import com.github.lucasls.subscriptions.domain.product.ProductRepository
import com.github.lucasls.subscriptions.domain.subscription.SubscriptionRepository
import com.github.lucasls.subscriptions.domain.value.SubscriptionStatus
import com.github.lucasls.subscriptions.persistence.jpa.ProductJpaRepository
import com.github.lucasls.subscriptions.persistence.jpa.entity.Price
import com.github.lucasls.subscriptions.persistence.jpa.entity.Product
import mu.KotlinLogging
import org.joda.money.Money
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.UUID
import javax.annotation.PostConstruct

@Profile("demo")
@Component
class DemoDataSetup(
    val productJpaRepository: ProductJpaRepository,
    val productRepository: ProductRepository,
    val subscriptionRepository: SubscriptionRepository,
) {
    private val logger = KotlinLogging.logger {}

    @PostConstruct
    fun onInit() {
        logger.info { "Creating demo data" }

        if (productJpaRepository.count() > 0) {
            logger.info { "Database already has data" }
            return
        }

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
        val quarterlyProduct = productRepository.findByCode("QUARTERLY")!!

        // Active subscription
        subscriptionRepository.create(
            userId = UUID.fromString("00000000-0000-0000-0000-000000000001"),
            subscription = Subscription(
                productSnapshot = annualProduct,
                createdAt = OffsetDateTime.parse("2020-06-01T16:00Z"),
            )
        )

        // Active subscription with a 10 days pause
        subscriptionRepository.create(
            userId = UUID.fromString("00000000-0000-0000-0000-000000000002"),
            subscription = Subscription(
                productSnapshot = annualProduct,
                createdAt = OffsetDateTime.parse("2020-06-01T16:00Z"),
                statusChanges = listOf(
                    StatusChange(OffsetDateTime.parse("2020-06-01T16:00Z"), SubscriptionStatus.ACTIVE),
                    StatusChange(OffsetDateTime.parse("2020-06-10T10:00Z"), SubscriptionStatus.PAUSED),
                    StatusChange(OffsetDateTime.parse("2020-06-20T10:00Z"), SubscriptionStatus.ACTIVE),
                )
            )
        )

        // Paused subscription
        subscriptionRepository.create(
            userId = UUID.fromString("00000000-0000-0000-0000-000000000003"),
            subscription = Subscription(
                productSnapshot = annualProduct,
                createdAt = OffsetDateTime.parse("2020-06-01T16:00Z"),
                statusChanges = listOf(
                    StatusChange(OffsetDateTime.parse("2020-06-01T16:00Z"), SubscriptionStatus.ACTIVE),
                    StatusChange(OffsetDateTime.parse("2021-01-10T10:00Z"), SubscriptionStatus.PAUSED),
                )
            )
        )

        // Active subscription with old product proce and tax
        subscriptionRepository.create(
            userId = UUID.fromString("00000000-0000-0000-0000-000000000004"),
            subscription = Subscription(
                productSnapshot = annualProduct.copy(
                    price = Money.parse("EUR 78.99"),
                    taxRate = 0.1
                ),
                createdAt = OffsetDateTime.parse("2020-04-01T16:00Z"),
            )
        )

        // Expired subscription
        subscriptionRepository.create(
            userId = UUID.fromString("00000000-0000-0000-0000-000000000005"),
            subscription = Subscription(
                productSnapshot = quarterlyProduct,
                createdAt = OffsetDateTime.parse("2020-08-01T16:00Z"),
            )
        )
    }
}