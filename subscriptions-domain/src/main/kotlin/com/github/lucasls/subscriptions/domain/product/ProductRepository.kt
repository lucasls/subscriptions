package com.github.lucasls.subscriptions.domain.product

interface ProductRepository {
    fun listAll(): List<Product>
    fun findByCode(code: String): Product?
}