package com.github.lucasls.subscriptions.domain.subscription

import com.github.lucasls.subscriptions.domain.model.Subscription
import com.github.lucasls.subscriptions.domain.payment.PaymentGateway
import com.github.lucasls.subscriptions.domain.payment.PaymentGateway.CreateTransactionResult
import com.github.lucasls.subscriptions.domain.product.ProductRepository
import com.github.lucasls.subscriptions.domain.value.SubscriptionStatus
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class SubscriptionUseCases(
    val productRepository: ProductRepository,
    val subscriptionRepository: SubscriptionRepository,
    val paymentGateway: PaymentGateway,
) {

    fun create(
        userId: UUID,
        productCode: String,
        paymentToken: String,
        paymentProvider: String
    ): CreateSubscriptionResult {
        val product = productRepository.findByCode(productCode)
            ?: return CreateSubscriptionResult.ProductNotFound

        val subscription = subscriptionRepository.findLatestByUserId(userId)

        if (subscription != null && subscription.status.preventsNewSubscription) {
            return CreateSubscriptionResult.UserAlreadySubscribed
        }

        val createTransactionResult = paymentGateway.createTransaction(
            value = product.price,
            token = paymentToken,
            provider = paymentProvider
        )

        if (createTransactionResult is CreateTransactionResult.PaymentDeclined) {
            return CreateSubscriptionResult.PaymentDeclined(createTransactionResult.reason)
        }

        val newSubscription = Subscription(
            productSnapshot = product
        )

        subscriptionRepository.create(userId, newSubscription)

        return CreateSubscriptionResult.Successful(newSubscription)
    }

    fun findByUserId(userId: UUID): Subscription? {
        return subscriptionRepository.findLatestByUserId(userId)
    }

    fun setStatus(userId: UUID, status: SubscriptionStatus): SetStatusResult {
        if (status.isFinal) {
            return SetStatusResult.StatusNotAllowed
        }

        val subscription = subscriptionRepository.findLatestByUserId(userId)
            ?.takeUnless { it.status.isFinal }
            ?: return SetStatusResult.SubscriptionNotFound

        if (subscription.status == status) {
            return SetStatusResult.AlreadySet
        }

        subscriptionRepository.update(
            userId = userId,
            subscription = subscription.withStatus(status)
        )

        return SetStatusResult.Successful
    }

    fun cancel(userId: UUID): CancelResult {
        val subscription = subscriptionRepository.findLatestByUserId(userId)
            ?.takeUnless { it.status.isFinal }
            ?: return CancelResult.SubscriptionNotFound

        val newSubscription = subscription.withStatus(SubscriptionStatus.CANCELED)
        subscriptionRepository.update(
            userId = userId,
            subscription = newSubscription

        )

        return CancelResult.Successful(newSubscription)
    }

    sealed class CreateSubscriptionResult {
        data class Successful(
            val subscription: Subscription
        ) : CreateSubscriptionResult()

        data class PaymentDeclined(
            val reason: String
        ) : CreateSubscriptionResult()

        object ProductNotFound : CreateSubscriptionResult()
        object UserAlreadySubscribed : CreateSubscriptionResult()
    }

    enum class SetStatusResult {
        Successful,
        AlreadySet,
        StatusNotAllowed,
        SubscriptionNotFound,
    }

    sealed class CancelResult {
        data class Successful(val subscription: Subscription) : CancelResult()
        object SubscriptionNotFound : CancelResult()
    }
}