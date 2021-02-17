package com.github.lucasls.subscriptions.domain.subscription

import com.github.lucasls.subscriptions.domain.model.Product
import com.github.lucasls.subscriptions.domain.model.Subscription
import com.github.lucasls.subscriptions.domain.payment.PaymentGateway
import com.github.lucasls.subscriptions.domain.payment.PaymentGateway.CreateTransactionResult
import com.github.lucasls.subscriptions.domain.product.ProductRepository
import com.github.lucasls.subscriptions.domain.subscription.SubscriptionUseCases.CreateSubscriptionResult
import com.github.lucasls.subscriptions.domain.value.SubscriptionStatus
import io.kotest.assertions.throwables.shouldThrowMessage
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.joda.money.Money
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.Period
import java.util.UUID

internal class SubscriptionUseCasesTest {
    val subscriptionRepository: SubscriptionRepository = mockk()
    val paymentGateway: PaymentGateway = mockk()
    val productRepository: ProductRepository = mockk()

    val subject: SubscriptionUseCases = SubscriptionUseCases(
        paymentGateway = paymentGateway,
        productRepository = productRepository,
        subscriptionRepository = subscriptionRepository
    )

    @Nested
    inner class CreateSubscription {
        val product = Product(
            code = "ANNUAL",
            name = "Annual Payment",
            price = Money.parse("EUR 83.99"),
            subscriptionPeriod = Period.ofMonths(12),
            taxRate = 0.19
        )

        val subscription = Subscription(
            productSnapshot = product,
        )

        @Test
        internal fun `should return it when product not found`() {
            every { productRepository.findByCode("ANNUAL") } returns null
            val result = subject.create(
                userId = USER_ID,
                productCode = "ANNUAL",
                paymentToken = "token",
                paymentProvider = "PAYPAL"
            )
            result shouldBe CreateSubscriptionResult.ProductNotFound
        }

        @Test
        internal fun `should return it when user already subscribed`() = runBlocking {
            forAll(
                row(SubscriptionStatus.ACTIVE),
                row(SubscriptionStatus.PAUSED),
            ) { status ->
                every { productRepository.findByCode("ANNUAL") } returns product
                every { subscriptionRepository.findLatestByUserId(USER_ID) } returns subscription.withStatus(status)

                val result = subject.create(
                    userId = USER_ID,
                    productCode = "ANNUAL",
                    paymentToken = "token",
                    paymentProvider = "PAYPAL"
                )

                result shouldBe CreateSubscriptionResult.UserAlreadySubscribed
            }
        }

        @Test
        internal fun `should not return user already subscribed`() = runBlocking {
            forAll(
                row(subscription.withStatus(SubscriptionStatus.CANCELED)),
                row(subscription.withStatus(SubscriptionStatus.EXPIRED)),
                row(null),
            ) { subscription ->
                every { productRepository.findByCode("ANNUAL") } returns product
                every { subscriptionRepository.findLatestByUserId(USER_ID) } returns subscription
                every { paymentGateway.createTransaction(any(), any(), any()) } throws RuntimeException("CHECKPOINT")

                shouldThrowMessage("CHECKPOINT") {
                    subject.create(
                        userId = USER_ID,
                        productCode = "ANNUAL",
                        paymentToken = "token",
                        paymentProvider = "PAYPAL"
                    )
                }
            }
        }

        @Test
        internal fun `should return it when payment is declined`() {
            every { productRepository.findByCode("ANNUAL") } returns product
            every { subscriptionRepository.findLatestByUserId(USER_ID) } returns null
            every { paymentGateway.createTransaction(any(), any(), any()) } returns
                CreateTransactionResult.PaymentDeclined("Payment rejected")

            val result = subject.create(
                userId = USER_ID,
                productCode = "ANNUAL",
                paymentToken = "token",
                paymentProvider = "PAYPAL"
            )

            result shouldBe CreateSubscriptionResult.PaymentDeclined("Payment rejected")
        }

        @Test
        internal fun `should create a subscription and return it`() {
            every { productRepository.findByCode("ANNUAL") } returns product
            every { subscriptionRepository.findLatestByUserId(USER_ID) } returns null
            every { paymentGateway.createTransaction(any(), any(), any()) } returns
                CreateTransactionResult.Successful(UUID.randomUUID())
            every { subscriptionRepository.create(USER_ID, any()) } returns Unit

            val result = subject.create(
                userId = USER_ID,
                productCode = "ANNUAL",
                paymentToken = "token",
                paymentProvider = "PAYPAL"
            )

            result.shouldBeInstanceOf<CreateSubscriptionResult.Successful>()
            result.subscription.productSnapshot shouldBe product
            result.subscription.status shouldBe SubscriptionStatus.ACTIVE

            verify { subscriptionRepository.create(USER_ID, result.subscription) }
        }
    }

    companion object {
        val USER_ID: UUID = UUID.fromString("fb7b6c42-e94b-4c6a-b20c-5ce5c5e08ba4")
        val SUBSCRIPTION_ID: UUID = UUID.fromString("97df0645-a9fd-4b4b-be93-259620a12edc")
    }
}