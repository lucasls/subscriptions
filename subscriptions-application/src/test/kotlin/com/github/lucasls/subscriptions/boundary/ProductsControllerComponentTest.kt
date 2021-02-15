package com.github.lucasls.subscriptions.boundary

import com.github.lucasls.subscriptions.boundary.dto.Price
import com.github.lucasls.subscriptions.boundary.dto.Product
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
internal class ProductsControllerComponentTest(
    @Autowired
    private val productsController: ProductsController
) {
    @Test
    internal fun `should list all products`() {
        val productsResponse = productsController.listProducts()

        productsResponse shouldBe ProductsController.ListProductsResponse(
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
}