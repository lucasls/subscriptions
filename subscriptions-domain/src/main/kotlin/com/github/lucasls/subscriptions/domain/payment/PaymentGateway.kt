package com.github.lucasls.subscriptions.domain.payment

import org.joda.money.Money
import java.util.UUID

interface PaymentGateway {
    fun createTransaction(value: Money, token: String, provider: String): CreateTransactionResult

    sealed class CreateTransactionResult {
        class Successful(val transactionId: UUID) : CreateTransactionResult()
        class PaymentDeclined(val reason: String) : CreateTransactionResult()
    }
}