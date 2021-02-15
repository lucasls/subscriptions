package com.github.lucasls.subscriptions.boundary

import com.github.lucasls.subscriptions.DomainProduct
import com.github.lucasls.subscriptions.boundary.BoundaryMappers.Companion.fromDomain
import com.github.lucasls.subscriptions.boundary.dto.Product
import com.github.lucasls.subscriptions.domain.usecase.ProductUseCases
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Component
@RestController
@RequestMapping("/v1/products")
class ProductsController(
    val productUseCases: ProductUseCases
) {
    data class ListProductsResponse(
        val products: List<Product>
    )

    @GetMapping("/")
    fun listProducts(): ListProductsResponse {
        val products: List<DomainProduct> = productUseCases.listAll()

        return ListProductsResponse(
            products = products.map { it.fromDomain() }
        )
    }
}