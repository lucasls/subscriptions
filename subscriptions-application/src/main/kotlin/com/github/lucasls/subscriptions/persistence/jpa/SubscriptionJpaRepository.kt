package com.github.lucasls.subscriptions.persistence.jpa

import com.github.lucasls.subscriptions.persistence.jpa.entity.Subscription
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface SubscriptionJpaRepository : CrudRepository<Subscription, UUID> {
    fun findFirstByUserIdOrderByCreatedAtDesc(userId: UUID): Subscription?
}