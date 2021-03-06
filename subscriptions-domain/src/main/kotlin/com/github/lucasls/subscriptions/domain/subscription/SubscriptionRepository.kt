package com.github.lucasls.subscriptions.domain.subscription

import java.util.UUID

interface SubscriptionRepository {
    fun findLatestByUserId(userId: UUID): Subscription?
    fun create(userId: UUID, subscription: Subscription)
    fun update(userId: UUID, subscription: Subscription)
}
