package com.github.lucasls.subscriptions.domain.model

import java.util.UUID

data class User(
    val id: UUID,
    val activeSubscription: Subscription?
)
