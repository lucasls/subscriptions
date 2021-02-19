package com.github.lucasls.subscriptions.domain.subscription

import com.github.lucasls.subscriptions.domain.payment.PaymentGateway
import com.github.lucasls.subscriptions.domain.payment.PaymentGateway.CreateTransactionResult
import com.github.lucasls.subscriptions.domain.product.ProductRepository
import mu.KotlinLogging.logger
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class SubscriptionUseCases(
    val productRepository: ProductRepository,
    val subscriptionRepository: SubscriptionRepository,
    val paymentGateway: PaymentGateway,
) {
    private val logger = logger {}

    fun create(
        userId: UUID,
        productCode: String,
        paymentToken: String,
        paymentProvider: String
    ): CreateSubscriptionResult {
        logger.info { "Create subscription for user $userId, product $productCode" }
        val product = productRepository.findByCode(productCode)
            ?: return CreateSubscriptionResult.ProductNotFound.also {
                logger.info { "Product $productCode not found" }
            }

        logger.info { "Look for existing subscription" }
        val subscription = subscriptionRepository.findLatestByUserId(userId)

        if (subscription != null && subscription.status.preventsNewSubscription) {
            return CreateSubscriptionResult.UserAlreadySubscribed.also {
                logger.info { "User already has a subscription" }
            }
        }

        logger.info { "Create payment transaction" }
        val createTransactionResult = paymentGateway.createTransaction(
            value = product.price,
            token = paymentToken,
            provider = paymentProvider
        )

        when (createTransactionResult) {
            is CreateTransactionResult.PaymentDeclined ->
                return CreateSubscriptionResult.PaymentDeclined(createTransactionResult.reason).also {
                    logger.info { "Payment declined" }
                }
            is CreateTransactionResult.Successful ->
                logger.info { "Payment transaction created with ID ${createTransactionResult.transactionId}" }
        }

        logger.info { "Persist new subscription" }
        val newSubscription = Subscription(
            productSnapshot = product
        )

        subscriptionRepository.create(userId, newSubscription)

        return CreateSubscriptionResult.Successful(newSubscription).also {
            logger.info { "Subscription successfully created" }
        }
    }

    fun findLatestByUserId(userId: UUID): Subscription? = subscriptionRepository.findLatestByUserId(userId)

    fun setStatus(userId: UUID, status: SubscriptionStatus): SetStatusResult {
        logger.info { "Update subscription status for user $userId to $status" }
        if (!status.canBeSet) {
            return SetStatusResult.StatusNotAllowed.also {
                logger.info { "Status can't be set" }
            }
        }

        logger.info { "Find user's latest subscription" }
        val subscription = subscriptionRepository.findLatestByUserId(userId)
            ?.takeUnless { it.status.isFinal }
            ?: return SetStatusResult.SubscriptionNotFound.also {
                logger.info { "Subscription not found" }
            }

        if (subscription.status == status) {
            return SetStatusResult.AlreadySet.also {
                logger.info { "Status was already set" }
            }
        }

        logger.info { "Persist new status" }
        subscriptionRepository.update(
            userId = userId,
            subscription = subscription.withStatus(status)
        )

        return SetStatusResult.Successful.also {
            logger.info { "Status successfully set" }
        }
    }

    fun cancel(userId: UUID): CancelResult {
        logger.info { "Cancel subscription for user $userId" }
        val subscription = subscriptionRepository.findLatestByUserId(userId)
            ?.takeUnless { it.status.isFinal }
            ?: return CancelResult.SubscriptionNotFound.also {
                logger.info { "Subscription not found" }
            }

        val newSubscription = subscription.withStatus(SubscriptionStatus.CANCELED)

        logger.info { "Persist new status" }
        subscriptionRepository.update(
            userId = userId,
            subscription = newSubscription
        )

        return CancelResult.Successful(newSubscription).also {
            logger.info { "Subscription cancelled" }
        }
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
