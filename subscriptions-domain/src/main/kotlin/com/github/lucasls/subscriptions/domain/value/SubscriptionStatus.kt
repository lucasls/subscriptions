package com.github.lucasls.subscriptions.domain.value

enum class SubscriptionStatus(
    val preventsNewSubscription: Boolean
) {
    ACTIVE(preventsNewSubscription = true),
    PAUSED(preventsNewSubscription = true),
    EXPIRED(preventsNewSubscription = false),
    CANCELED(preventsNewSubscription = false),
}