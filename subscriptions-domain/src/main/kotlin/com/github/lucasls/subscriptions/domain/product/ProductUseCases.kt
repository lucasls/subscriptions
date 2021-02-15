package com.github.lucasls.subscriptions.domain.product

import org.springframework.stereotype.Component

@Component
class ProductUseCases(
    val productRepository: ProductRepository
) : ProductCrud by productRepository