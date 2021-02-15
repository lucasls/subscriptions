package com.github.lucasls.subscriptions.domain.subscription

import com.github.lucasls.subscriptions.domain.model.Subscription
import com.github.lucasls.subscriptions.domain.payment.PaymentGateway
import com.github.lucasls.subscriptions.domain.payment.PaymentGateway.CreateTransactionResult
import com.github.lucasls.subscriptions.domain.product.ProductRepository
import com.github.lucasls.subscriptions.domain.subscription.SubscriptionUseCases.CreateSubscriptionResult.PaymentDeclined
import com.github.lucasls.subscriptions.domain.subscription.SubscriptionUseCases.CreateSubscriptionResult.ProductNotFound
import com.github.lucasls.subscriptions.domain.subscription.SubscriptionUseCases.CreateSubscriptionResult.Successful
import com.github.lucasls.subscriptions.domain.subscription.SubscriptionUseCases.CreateSubscriptionResult.UserAlreadySubscribed
import com.github.lucasls.subscriptions.domain.value.SubscriptionStatus
import com.github.lucasls.subscriptions.domain.value.SubscriptionStatus.CANCELED
import com.github.lucasls.subscriptions.domain.value.SubscriptionStatus.EXPIRED
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
            ?: return ProductNotFound

        val subscription = subscriptionRepository.findByUserId(userId)

        if (subscription != null && subscription.status.preventsNewSubscription) {
            return UserAlreadySubscribed
        }

        val createTransactionResult = paymentGateway.createTransaction(
            value = product.price,
            token = paymentToken,
            provider = paymentProvider
        )

        if (createTransactionResult is CreateTransactionResult.PaymentDeclined) {
            return PaymentDeclined(createTransactionResult.reason)
        }

        val newSubscription = Subscription(
            productSnapshot = product
        )

        subscriptionRepository.create(userId, newSubscription)

        return Successful(newSubscription)
    }

    fun findByUserId(userId: UUID): Subscription? {
        return subscriptionRepository.findByUserId(userId)
    }

    fun setStatus(userId: UUID, status: SubscriptionStatus): SetStatusResult {
        if (status in setOf(EXPIRED, CANCELED)) {
            return SetStatusResult.StatusNotAllowed
        }

        val subscription = subscriptionRepository.findByUserId(userId)
            ?.takeUnless { it.status.isFinal }
            ?: return SetStatusResult.SubscriptionNotFound

        if (subscription.status == status) {
            return SetStatusResult.AlreadySet
        }

        subscriptionRepository.update(
            userId = userId,
            subscription = subscription.changeStatusTo(status)
        )

        return SetStatusResult.Successful
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

    sealed class SetStatusResult {
        object Successful : SetStatusResult()
        object AlreadySet : SetStatusResult()
        object StatusNotAllowed : SetStatusResult()
        object SubscriptionNotFound : SetStatusResult()
    }
}