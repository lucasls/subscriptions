package com.github.lucasls.subscriptions.boundary

import com.github.lucasls.subscriptions.boundary.BoundaryMappers.Companion.fromDomain
import com.github.lucasls.subscriptions.boundary.dto.Subscription
import com.github.lucasls.subscriptions.domain.subscription.SubscriptionStatus
import com.github.lucasls.subscriptions.domain.subscription.SubscriptionUseCases
import com.github.lucasls.subscriptions.domain.subscription.SubscriptionUseCases.CancelResult
import com.github.lucasls.subscriptions.domain.subscription.SubscriptionUseCases.CreateSubscriptionResult
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import java.util.UUID

@RestController
@RequestMapping("/v1/users/{userId}/subscription")
class SubscriptionController(
    val subscriptionUseCases: SubscriptionUseCases
) {

    data class CreateSubscriptionRequest(
        val paymentToken: String,
        val paymentProvider: String,
        val productCode: String,
    )

    @PostMapping("")
    fun createTransaction(
        @PathVariable userId: UUID,
        @RequestBody request: CreateSubscriptionRequest
    ): Subscription {
        val result: CreateSubscriptionResult = subscriptionUseCases.create(
            userId = userId,
            productCode = request.productCode,
            paymentToken = request.paymentToken,
            paymentProvider = request.paymentProvider
        )

        val subscription = when (result) {
            is CreateSubscriptionResult.UserAlreadySubscribed ->
                throw ResponseStatusException(HttpStatus.BAD_REQUEST, "User is already subscribed")
            is CreateSubscriptionResult.PaymentDeclined ->
                throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Payment Declined: ${result.reason}")
            is CreateSubscriptionResult.ProductNotFound ->
                throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Product does not exist")
            is CreateSubscriptionResult.Successful ->
                result.subscription
        }

        return subscription.fromDomain()
    }

    @GetMapping("")
    fun findSubscription(@PathVariable userId: UUID): Subscription {
        val subscription = subscriptionUseCases.findLatestByUserId(userId)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)

        return subscription.fromDomain()
    }

    @DeleteMapping("")
    fun cancelSubscription(@PathVariable userId: UUID): Subscription {
        val result = subscriptionUseCases.cancel(userId)
        return when (result) {
            is CancelResult.Successful -> result.subscription.fromDomain()
            is CancelResult.SubscriptionNotFound -> throw ResponseStatusException(HttpStatus.NOT_FOUND)
        }
    }

    @PutMapping("/status")
    fun setSubscriptionStatus(
        @PathVariable userId: UUID,
        @RequestBody subscriptionStatus: SubscriptionStatus
    ): SubscriptionStatus {
        return when (subscriptionUseCases.setStatus(userId, subscriptionStatus)) {
            SubscriptionUseCases.SetStatusResult.Successful ->
                subscriptionStatus
            SubscriptionUseCases.SetStatusResult.AlreadySet ->
                throw ResponseStatusException(HttpStatus.NOT_MODIFIED, "Already set to $subscriptionStatus")
            SubscriptionUseCases.SetStatusResult.StatusNotAllowed ->
                throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Use DELETE instead")
            SubscriptionUseCases.SetStatusResult.SubscriptionNotFound ->
                throw ResponseStatusException(HttpStatus.NOT_FOUND)
        }
    }
}
