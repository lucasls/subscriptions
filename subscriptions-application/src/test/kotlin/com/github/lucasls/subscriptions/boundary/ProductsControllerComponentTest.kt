package com.github.lucasls.subscriptions.boundary

import com.github.lucasls.subscriptions.boundary.dto.Price
import com.github.lucasls.subscriptions.boundary.dto.Product
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

@SpringBootTest
internal class ProductsControllerComponentTest(
    @Autowired
    private val subject: ProductsController
) {
    @Test
    internal fun `should list all products`() {
        val result = subject.listProducts()

        result shouldBe ProductsController.ListProductsResponse(
            products = listOf(
                Product(
                    code = "ANNUAL",
                    name = "Annual Payment",
                    price = Price(
                        value = "83.99",
                        unit = "EUR"
                    ),
                    subscriptionPeriodMonths = 12,
                    taxRate = "0.07",
                    tax = Price(
                        value = "5.87",
                        unit = "EUR"
                    )
                ),
                Product(
                    code = "SEMI_ANNUAL",
                    name = "Semi-Annual Payment",
                    price = Price(
                        value = "59.99",
                        unit = "EUR"
                    ),
                    subscriptionPeriodMonths = 6,
                    taxRate = "0.19",
                    tax = Price(
                        value = "11.39",
                        unit = "EUR"
                    )
                ),
                Product(
                    code = "QUARTERLY",
                    name = "Quarterly payment",
                    price = Price(
                        value = "38.99",
                        unit = "EUR"
                    ),
                    subscriptionPeriodMonths = 3,
                    taxRate = "0.19",
                    tax = Price(
                        value = "7.40",
                        unit = "EUR"
                    )
                ),
            )
        )
    }

    @Test
    internal fun `should find an existing product`() {
        val result = subject.findProductByCode("ANNUAL")
        result shouldBe Product(
            code = "ANNUAL",
            name = "Annual Payment",
            price = Price(
                value = "83.99",
                unit = "EUR"
            ),
            subscriptionPeriodMonths = 12,
            taxRate = "0.07",
            tax = Price(
                value = "5.87",
                unit = "EUR"
            )
        )
    }

    @Test
    internal fun `should throw not found exception when product is not found`() {
        val exception = shouldThrow<ResponseStatusException> {
            subject.findProductByCode("EVERY_5_YEARS")
        }

        exception.status shouldBe HttpStatus.NOT_FOUND
    }
}