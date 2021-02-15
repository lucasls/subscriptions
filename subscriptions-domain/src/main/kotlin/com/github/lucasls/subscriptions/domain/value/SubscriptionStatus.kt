package com.github.lucasls.subscriptions.domain.value

enum class SubscriptionStatus(
    val preventsNewSubscription: Boolean,
    val isFinal: Boolean
) {
    ACTIVE(preventsNewSubscription = true, isFinal = false),
    PAUSED(preventsNewSubscription = true, isFinal = false),
    EXPIRED(preventsNewSubscription = false, isFinal = true),
    CANCELED(preventsNewSubscription = false, isFinal = true),
}