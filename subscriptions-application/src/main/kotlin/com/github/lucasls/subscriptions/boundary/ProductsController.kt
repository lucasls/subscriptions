package com.github.lucasls.subscriptions.boundary

import com.github.lucasls.subscriptions.boundary.dto.Price
import com.github.lucasls.subscriptions.boundary.dto.Product
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Component
@RestController
@RequestMapping("/v1/products")
class ProductsController {
    data class ListProductsResponse(
        val products: List<Product>
    )

    @GetMapping("/")
    fun listProducts(): ListProductsResponse = ListProductsResponse(
        products = listOf(
            Product(
                code = "ANNUAL",
                name = "Annual Payment",
                price = Price(
                    value = "83.99",
                    unit = "EUR"
                ),
                subscriptionPeriodMonths = 12
            ),
            Product(
                code = "SEMI_ANNUAL",
                name = "Semi-Annual Payment",
                price = Price(
                    value = "59.99",
                    unit = "EUR"
                ),
                subscriptionPeriodMonths = 6
            ),
            Product(
                code = "QUARTERLY",
                name = "Quarterly payment",
                price = Price(
                    value = "38.99",
                    unit = "EUR"
                ),
                subscriptionPeriodMonths = 3
            ),
        )
    )
}