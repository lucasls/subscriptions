package com.github.lucasls.subscriptions.persistence

import com.github.lucasls.subscriptions.domain.model.Subscription
import com.github.lucasls.subscriptions.domain.subscription.SubscriptionRepository
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class InMemorySubscriptionRepository : SubscriptionRepository {
    val subscriptions = mutableMapOf<UUID, Subscription>()

    override fun findByUserId(userId: UUID): Subscription? = subscriptions[userId]?.copy()

    override fun create(userId: UUID, subscription: Subscription) {
        subscriptions[userId] = subscription.copy()
    }

    override fun update(userId: UUID, subscription: Subscription) {
        subscriptions[userId] = subscription.copy()
    }
}