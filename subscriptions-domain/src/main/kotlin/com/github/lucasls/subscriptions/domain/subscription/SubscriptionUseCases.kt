package com.github.lucasls.subscriptions.domain.subscription

import com.github.lucasls.subscriptions.domain.model.Subscription
import com.github.lucasls.subscriptions.domain.payment.PaymentGateway
import com.github.lucasls.subscriptions.domain.payment.PaymentGateway.CreateTransactionResult
import com.github.lucasls.subscriptions.domain.product.ProductRepository
import com.github.lucasls.subscriptions.domain.subscription.SubscriptionUseCases.CreateSubscriptionResult.PaymentDeclined
import com.github.lucasls.subscriptions.domain.subscription.SubscriptionUseCases.CreateSubscriptionResult.ProductNotFound
import com.github.lucasls.subscriptions.domain.subscription.SubscriptionUseCases.CreateSubscriptionResult.Successful
import com.github.lucasls.subscriptions.domain.subscription.SubscriptionUseCases.CreateSubscriptionResult.UserAlreadySubscribed
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
}