package com.github.lucasls.subscriptions.boundary

import com.github.lucasls.subscriptions.DomainProduct
import com.github.lucasls.subscriptions.boundary.BoundaryMappers.Companion.fromDomain
import com.github.lucasls.subscriptions.boundary.dto.Product
import com.github.lucasls.subscriptions.domain.product.ProductUseCases
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@Component
@RestController
@RequestMapping("/v1/products")
class ProductsController(
    val productUseCases: ProductUseCases
) {
    @GetMapping("/")
    fun listProducts(): ListProductsResponse {
        val products: List<DomainProduct> = productUseCases.listAll()

        return ListProductsResponse(
            products = products.map { it.fromDomain() }
        )
    }

    @GetMapping("/{code}")
    fun findProductByCode(@PathVariable code: String): Product {
        val product = productUseCases.findByCode(code)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)

        return product.fromDomain()
    }

    data class ListProductsResponse(
        val products: List<Product>
    )
}
