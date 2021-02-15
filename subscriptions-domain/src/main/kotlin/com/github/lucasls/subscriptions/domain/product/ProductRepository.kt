package com.github.lucasls.subscriptions.domain.product

import com.github.lucasls.subscriptions.domain.model.Product

interface ProductRepository : ProductCrud {
    override fun listAll(): List<Product>
    override fun findByCode(code: String): Product?
}