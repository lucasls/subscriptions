package com.github.lucasls.subscriptions.domain.subscription

import com.github.lucasls.subscriptions.domain.payment.PaymentGateway
import com.github.lucasls.subscriptions.domain.payment.PaymentGateway.CreateTransactionResult
import com.github.lucasls.subscriptions.domain.product.Product
import com.github.lucasls.subscriptions.domain.product.ProductRepository
import com.github.lucasls.subscriptions.domain.subscription.SubscriptionUseCases.CancelResult
import com.github.lucasls.subscriptions.domain.subscription.SubscriptionUseCases.CreateSubscriptionResult
import com.github.lucasls.subscriptions.domain.subscription.SubscriptionUseCases.SetStatusResult
import io.kotest.assertions.throwables.shouldThrowMessage
import io.kotest.data.blocking.forAll
import io.kotest.data.row
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.joda.money.Money
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.Period
import java.util.UUID

internal class SubscriptionUseCasesTest {
    val subscriptionRepository: SubscriptionRepository = mockk(relaxUnitFun = true)
    val paymentGateway: PaymentGateway = mockk()
    val productRepository: ProductRepository = mockk()

    val subject: SubscriptionUseCases = SubscriptionUseCases(
        paymentGateway = paymentGateway,
        productRepository = productRepository,
        subscriptionRepository = subscriptionRepository
    )

    private val product = Product(
        code = "ANNUAL",
        name = "Annual Payment",
        price = Money.parse("EUR 83.99"),
        subscriptionPeriod = Period.ofMonths(12),
        taxRate = 0.19
    )

    private val subscription = Subscription(
        id = SUBSCRIPTION_ID,
        productSnapshot = product,
    )

    @Nested
    inner class CreateSubscriptionTest {
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
        internal fun `should return it when user already subscribed`() {
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
        internal fun `should not return user already subscribed`() {
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
                CreateTransactionResult.Successful()

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

    @Nested
    inner class SetStatusTest {
        @Test
        internal fun `should not allow setting to a status that can't be set`() = forAll(
            row(SubscriptionStatus.EXPIRED),
            row(SubscriptionStatus.CANCELED),
        ) { status ->
            val result = subject.setStatus(USER_ID, status)
            result shouldBe SetStatusResult.StatusNotAllowed
        }

        @Test
        internal fun `should not allow updating when subscription does not exist or is in a final state`() = forAll(
            row(null),
            row(SubscriptionStatus.EXPIRED),
            row(SubscriptionStatus.CANCELED),
        ) { status ->
            every {
                subscriptionRepository.findLatestByUserId(USER_ID)
            } returns status?.let { subscription.withStatus(it) }

            val result = subject.setStatus(USER_ID, SubscriptionStatus.PAUSED)

            result shouldBe SetStatusResult.SubscriptionNotFound
        }

        @Test
        internal fun `should return it when status is already set`() = forAll(
            row(SubscriptionStatus.ACTIVE),
            row(SubscriptionStatus.PAUSED),
        ) { status ->
            every {
                subscriptionRepository.findLatestByUserId(USER_ID)
            } returns subscription.withStatus(status)

            val result = subject.setStatus(USER_ID, status)

            result shouldBe SetStatusResult.AlreadySet
        }

        @Test
        internal fun `should update status`() = forAll(
            row(SubscriptionStatus.ACTIVE, SubscriptionStatus.PAUSED),
            row(SubscriptionStatus.PAUSED, SubscriptionStatus.ACTIVE),
        ) { currentStatus, newStatus ->
            every {
                subscriptionRepository.findLatestByUserId(USER_ID)
            } returns subscription.withStatus(currentStatus)

            val result = subject.setStatus(USER_ID, newStatus)

            verify {
                subscriptionRepository.update(
                    userId = USER_ID,
                    subscription = withArg {
                        it.id shouldBe subscription.id
                        it.status shouldBe newStatus
                    }
                )
            }

            result shouldBe SetStatusResult.Successful
        }
    }

    @Nested
    inner class CancelTest {
        @Test
        internal fun `should not return it when subscription does not exist or is in a final state`() = forAll(
            row(null),
            row(SubscriptionStatus.EXPIRED),
            row(SubscriptionStatus.CANCELED),
        ) { status ->
            every {
                subscriptionRepository.findLatestByUserId(USER_ID)
            } returns status?.let { subscription.withStatus(it) }

            val result = subject.cancel(USER_ID)

            result shouldBe CancelResult.SubscriptionNotFound
        }

        @Test
        internal fun `should cancel subscription`() = forAll(
            row(SubscriptionStatus.ACTIVE),
            row(SubscriptionStatus.PAUSED),
        ) { status ->
            every {
                subscriptionRepository.findLatestByUserId(USER_ID)
            } returns subscription.withStatus(status)

            val result = subject.cancel(USER_ID)

            verify {
                subscriptionRepository.update(
                    userId = USER_ID,
                    subscription = withArg {
                        it.id shouldBe subscription.id
                        it.status shouldBe SubscriptionStatus.CANCELED
                    }
                )
            }

            result.shouldBeInstanceOf<CancelResult.Successful>()
            result.subscription.id shouldBe subscription.id
            result.subscription.status shouldBe SubscriptionStatus.CANCELED
        }
    }

    companion object {
        val USER_ID: UUID = UUID.fromString("fb7b6c42-e94b-4c6a-b20c-5ce5c5e08ba4")
        val SUBSCRIPTION_ID: UUID = UUID.fromString("97df0645-a9fd-4b4b-be93-259620a12edc")
    }
}