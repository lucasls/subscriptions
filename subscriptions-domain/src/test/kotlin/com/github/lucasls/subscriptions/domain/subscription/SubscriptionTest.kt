package com.github.lucasls.subscriptions.domain.subscription

import com.github.lucasls.subscriptions.domain.product.Product
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.joda.money.Money
import org.junit.jupiter.api.Test
import java.time.OffsetDateTime
import java.time.Period
import java.time.temporal.ChronoUnit.SECONDS
import java.util.UUID

internal class SubscriptionTest {
    private val product = Product(
        code = "ANNUAL",
        name = "Annual Payment",
        price = Money.parse("EUR 83.99"),
        subscriptionPeriod = Period.ofMonths(12),
        taxRate = 0.19
    )

    @Test
    internal fun `should require at least one status change`() {
        shouldThrow<IllegalArgumentException> {
            Subscription(
                id = UUID.randomUUID(),
                productSnapshot = product,
                createdAt = OffsetDateTime.now(),
                statusChanges = emptyList()
            )
        }
    }

    @Test
    internal fun `should expire after subscription period`() {
        val subject = Subscription(
            id = UUID.randomUUID(),
            productSnapshot = product,
            createdAt = OffsetDateTime.parse("2021-02-15T16:00Z"),
        )

        subject.expiresAt shouldBe OffsetDateTime.parse("2022-02-15T16:00Z")
    }

    @Test
    internal fun `should expire after subscription period plus pauses`() {
        val createdAt = OffsetDateTime.parse("2021-02-15T16:00Z")
        val subject = Subscription(
            id = UUID.randomUUID(),
            productSnapshot = product,
            createdAt = createdAt,
            statusChanges = listOf(
                StatusChange(createdAt, SubscriptionStatus.ACTIVE),
                StatusChange(OffsetDateTime.parse("2021-02-16T16:00Z"), SubscriptionStatus.PAUSED),
                StatusChange(OffsetDateTime.parse("2021-02-18T16:00Z"), SubscriptionStatus.ACTIVE),
            )
        )

        subject.expiresAt shouldBe OffsetDateTime.parse("2022-02-17T16:00Z")
    }

    @Test
    internal fun `should expire after subscription with open pauses`() {
        val now = OffsetDateTime.now()
        val createdAt = now - Period.ofYears(1)
        val subject = Subscription(
            id = UUID.randomUUID(),
            productSnapshot = product,
            createdAt = createdAt,
            statusChanges = listOf(
                StatusChange(createdAt, SubscriptionStatus.ACTIVE),
                StatusChange(createdAt + Period.ofDays(10), SubscriptionStatus.PAUSED),
                StatusChange(createdAt + Period.ofDays(20), SubscriptionStatus.ACTIVE),
                StatusChange(now - Period.ofDays(15), SubscriptionStatus.PAUSED),
            )
        )
        subject.expiresAt.truncatedTo(SECONDS) shouldBe
            (now + Period.ofDays(25)).truncatedTo(SECONDS)
    }

    @Test
    internal fun `should return current status when not expired`() {
        val now = OffsetDateTime.now()
        val subject = Subscription(
            id = UUID.randomUUID(),
            productSnapshot = product,
            createdAt = now - Period.ofMonths(6)
        )
        subject.status shouldBe SubscriptionStatus.ACTIVE
    }

    @Test
    internal fun `should always return expired status when expired unless cancelled`() {
        val now = OffsetDateTime.now()
        val subject = Subscription(
            id = UUID.randomUUID(),
            productSnapshot = product,
            createdAt = now - Period.ofYears(3)
        )
        subject.status shouldBe SubscriptionStatus.EXPIRED
    }

    @Test
    internal fun `should always return cancelled even when expired`() {
        val now = OffsetDateTime.now()
        val createdAt = now - Period.ofYears(3)
        val subject = Subscription(
            id = UUID.randomUUID(),
            productSnapshot = product,
            createdAt = createdAt,
            statusChanges = listOf(
                StatusChange(createdAt, SubscriptionStatus.ACTIVE),
                StatusChange(createdAt + Period.ofDays(10), SubscriptionStatus.CANCELED),
            ),
        )
        subject.status shouldBe SubscriptionStatus.CANCELED
    }
}
