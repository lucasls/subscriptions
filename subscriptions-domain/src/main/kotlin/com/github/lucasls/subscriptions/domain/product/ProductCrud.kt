package com.github.lucasls.subscriptions.domain.product

import com.github.lucasls.subscriptions.domain.model.Product

interface ProductCrud {
    fun listAll(): List<Product>
    fun findByCode(code: String): Product?
}