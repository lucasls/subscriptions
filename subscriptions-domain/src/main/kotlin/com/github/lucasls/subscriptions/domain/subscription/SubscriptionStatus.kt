package com.github.lucasls.subscriptions.domain.subscription

enum class SubscriptionStatus(
    val isFinal: Boolean,
    val canBeSet: Boolean
) {
    ACTIVE(
        isFinal = false,
        canBeSet = true // A subscription can be freely unpaused
    ),
    PAUSED(
        isFinal = false,
        canBeSet = true // A subscription can be freely paused
    ),
    EXPIRED(
        isFinal = true,
        canBeSet = false // Expiration can only happen with time
    ),
    CANCELED(
        isFinal = true,
        canBeSet = false // A subscription must be cancelled to have this state
    ),

    ;

    /** Should only allow a subscription if subscription already ended */
    val preventsNewSubscription: Boolean = !isFinal
}
