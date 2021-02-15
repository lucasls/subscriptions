package com.github.lucasls.subscriptions.domain.common

sealed class DomainException : RuntimeException()

class ProductNotFoundException : DomainException()

class PaymentDeclinedException : DomainException()