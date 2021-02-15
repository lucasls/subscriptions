package com.github.lucasls.subscriptions.domain.repository

import com.github.lucasls.subscriptions.domain.model.Product

interface ProductRepository {
    fun listAll(): List<Product>
}