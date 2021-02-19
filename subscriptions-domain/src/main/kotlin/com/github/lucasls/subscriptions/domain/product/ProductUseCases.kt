package com.github.lucasls.subscriptions.domain.product

import org.springframework.stereotype.Component

@Component
class ProductUseCases(
    val productRepository: ProductRepository
) {
    fun listAll(): List<Product> = productRepository.listAll()
    fun findByCode(code: String): Product? = productRepository.findByCode(code)
}
