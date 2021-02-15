package com.github.lucasls.subscriptions.domain.usecase

import com.github.lucasls.subscriptions.domain.model.Product
import com.github.lucasls.subscriptions.domain.repository.ProductRepository
import org.springframework.stereotype.Component

@Component
class ProductUseCases(
    val productRepository: ProductRepository
) {

    fun listAll(): List<Product> = productRepository.listAll()

}