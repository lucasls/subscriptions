package com.github.lucasls.subscriptions.external

import com.github.lucasls.subscriptions.domain.payment.PaymentGateway
import com.github.lucasls.subscriptions.domain.payment.PaymentGateway.CreateTransactionResult
import com.github.lucasls.subscriptions.domain.payment.PaymentGateway.CreateTransactionResult.PaymentDeclined
import com.github.lucasls.subscriptions.domain.payment.PaymentGateway.CreateTransactionResult.Successful
import org.joda.money.Money
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class MockedPaymentGateway : PaymentGateway {
    override fun createTransaction(
        value: Money,
        token: String,
        provider: String
    ): CreateTransactionResult = if (token != "decline-transaction") {
        Successful(UUID.randomUUID())
    } else {
        PaymentDeclined("Payment was declined by the Payment Processor")
    }
}
