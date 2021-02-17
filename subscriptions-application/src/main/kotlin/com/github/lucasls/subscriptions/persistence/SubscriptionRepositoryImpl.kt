package com.github.lucasls.subscriptions.persistence

import com.github.lucasls.subscriptions.domain.model.Subscription
import com.github.lucasls.subscriptions.domain.subscription.SubscriptionRepository
import com.github.lucasls.subscriptions.persistence.PersistenceMappers.Companion.fromDomain
import com.github.lucasls.subscriptions.persistence.PersistenceMappers.Companion.toDomain
import com.github.lucasls.subscriptions.persistence.jpa.SubscriptionJpaRepository
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class SubscriptionRepositoryImpl(
    val subscriptionJpaRepository: SubscriptionJpaRepository
) : SubscriptionRepository {
    override fun findLatestByUserId(userId: UUID): Subscription? =
        subscriptionJpaRepository.findFirstByUserIdOrderByCreatedAtDesc(userId)
            ?.toDomain()

    override fun create(userId: UUID, subscription: Subscription) {
        subscriptionJpaRepository.save((userId to subscription).fromDomain())
    }

    override fun update(userId: UUID, subscription: Subscription) {
        subscriptionJpaRepository.save((userId to subscription).fromDomain())
    }
}