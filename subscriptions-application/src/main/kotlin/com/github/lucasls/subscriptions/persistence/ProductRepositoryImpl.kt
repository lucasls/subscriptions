package com.github.lucasls.subscriptions.persistence

import com.github.lucasls.subscriptions.DomainProduct
import com.github.lucasls.subscriptions.domain.product.ProductRepository
import com.github.lucasls.subscriptions.persistence.PersistenceMappers.Companion.toDomain
import com.github.lucasls.subscriptions.persistence.jpa.ProductJpaRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Component

@Component
class ProductRepositoryImpl(
    val productJpaRepository: ProductJpaRepository
) : ProductRepository {
    override fun listAll(): List<DomainProduct> =
        productJpaRepository
            .findAll()
            .map { it.toDomain() }

    override fun findByCode(code: String): DomainProduct? =
        productJpaRepository.findByIdOrNull(code)?.toDomain()
}
