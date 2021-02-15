package com.github.lucasls.subscriptions.domain.subscription

import com.github.lucasls.subscriptions.domain.model.Subscription
import java.util.UUID

interface SubscriptionRepository {
    fun findByUserId(userId: UUID): Subscription?
    fun create(userId: UUID, subscription: Subscription)
}