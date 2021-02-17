package com.github.lucasls.subscriptions.domain.product

import com.github.lucasls.subscriptions.domain.model.Product

interface ProductRepository {
    fun listAll(): List<Product>
    fun findByCode(code: String): Product?
}