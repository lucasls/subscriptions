package com.github.lucasls.subscriptions.domain.model

import com.github.lucasls.subscriptions.domain.value.SubscriptionStatus
import java.time.Duration
import java.time.OffsetDateTime
import java.util.UUID

data class Subscription(
    val id: UUID = UUID.randomUUID(),
    val productSnapshot: Product,
    val createdAt: OffsetDateTime = OffsetDateTime.now(),
    val statusChanges: List<StatusChange> = listOf(StatusChange(createdAt, SubscriptionStatus.ACTIVE)),
) {
    init {
        if (statusChanges.isEmpty()) {
            throw IllegalArgumentException("At least one status must be set")
        }
    }

    val expiresAt: OffsetDateTime by lazy {
        createdAt + productSnapshot.subscriptionPeriod + totalPausedDuration
    }

    val isExpired: Boolean by lazy {
        OffsetDateTime.now() > expiresAt
    }

    val status: SubscriptionStatus by lazy {
        if (isExpired) {
            SubscriptionStatus.EXPIRED
        } else {
            statusChanges.last().status
        }
    }

    internal val statusPeriods: List<StatusPeriod> by lazy {
        val currentStatus = statusChanges.last().copy(changedAt = OffsetDateTime.now())
        statusChanges
            .zip(statusChanges.drop(1) + currentStatus)
            .map { it.toStatusPeriod() }
    }

    private val totalPausedDuration: Duration by lazy {
        statusPeriods
            .filter { it.status == SubscriptionStatus.PAUSED }
            .map { it.duration }
            .reduceOrNull(Duration::plus)
            ?: Duration.ZERO
    }

    private fun Pair<StatusChange, StatusChange>.toStatusPeriod() = let { (from, to) ->
        StatusPeriod(
            startedAt = from.changedAt,
            endedAt = to.changedAt,
            status = from.status
        )
    }
}
