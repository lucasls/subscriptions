package com.github.lucasls.subscriptions.boundary

import com.github.lucasls.subscriptions.TestDataSetup
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@AutoConfigureMockMvc
@SpringBootTest
@Import(TestDataSetup::class)
internal class ProductsControllerComponentTest(
    @Autowired
    private val mockMvc: MockMvc
) {
    @Test
    internal fun `should list all products`() {
        mockMvc.get("/v1/products/").andExpect {
            content {
                """
                {
                  "products": [
                    {
                      "code": "ANNUAL",
                      "name": "Annual Payment",
                      "price": {
                        "value": "83.99",
                        "unit": "EUR"
                      },
                      "subscriptionPeriodMonths": 12,
                      "taxRate": "0.07",
                      "tax": {
                        "value": "5.87",
                        "unit": "EUR"
                      }
                    },
                    {
                      "code": "SEMI_ANNUAL",
                      "name": "Semi-Annual Payment",
                      "price": {
                        "value": "59.99",
                        "unit": "EUR"
                      },
                      "subscriptionPeriodMonths": 6,
                      "taxRate": "0.19",
                      "tax": {
                        "value": "11.39",
                        "unit": "EUR"
                      }
                    },
                    {
                      "code": "QUARTERLY",
                      "name": "Quarterly payment",
                      "price": {
                        "value": "38.99",
                        "unit": "EUR"
                      },
                      "subscriptionPeriodMonths": 3,
                      "taxRate": "0.19",
                      "tax": {
                        "value": "7.40",
                        "unit": "EUR"
                      }
                    }
                  ]
                }""".let { json(it) }
            }
        }
    }

    @Test
    internal fun `should find an existing product`() {
        mockMvc.get("/v1/products/ANNUAL").andExpect {
            content {
                """
                {
                  "code": "ANNUAL",
                  "name": "Annual Payment",
                  "price": {
                    "value": "83.99",
                    "unit": "EUR"
                  },
                  "subscriptionPeriodMonths": 12,
                  "taxRate": "0.07",
                  "tax": {
                    "value": "5.87",
                    "unit": "EUR"
                  }
                }
                """.let { json(it) }
            }
        }
    }

    @Test
    internal fun `should return 404 when product is not found`() {
        mockMvc.get("/v1/products/EVERY_5_YEARS").andExpect {
            status { isNotFound() }
        }
    }
}
