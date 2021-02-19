package com.github.lucasls.subscriptions.boundary

import com.github.lucasls.subscriptions.TestDataSetup
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.put
import org.springframework.transaction.annotation.Transactional

@AutoConfigureMockMvc
@SpringBootTest
@Import(TestDataSetup::class)
@Transactional
internal class SubscriptionControllerComponentTest(
    @Autowired
    private val mockMvc: MockMvc
) {
    @Test
    internal fun `should list active subscription`() {
        mockMvc.get("/v1/users/00000000-0000-0000-0000-000000000001/subscription").andExpect {
            content {
                """
                {
                  "productSnapshot": {
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
                  "status": "ACTIVE"
                }""".let { json(it) }
            }
        }
    }

    @Test
    internal fun `should return 404 when subscription is not found`() {
        mockMvc.get("/v1/users/00000000-0000-0000-0000-00000000000f/subscription").andExpect {
            status { isNotFound() }
        }
    }

    @Test
    internal fun `should delete active subscription`() {
        mockMvc.delete("/v1/users/00000000-0000-0000-0000-000000000001/subscription").andExpect {
            content {
                """
                {
                  "productSnapshot": {
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
                  "status": "CANCELED"
                }""".let { json(it) }
            }
        }
    }

    @Test
    internal fun `should change subscription status`() {
        mockMvc.put("/v1/users/00000000-0000-0000-0000-000000000001/subscription/status") {
            contentType = MediaType.APPLICATION_JSON
            content = """ "PAUSED" """.trim()
        }.andExpect {
            content {
                json(""" "PAUSED" """.trim())
            }
        }
    }

    @Test
    internal fun `should create subscription`() {
        mockMvc.post("/v1/users/00000000-0000-0000-0000-000000000002/subscription") {
            contentType = MediaType.APPLICATION_JSON
            content = """
                {
                  "paymentToken": "some-token",
                  "paymentProvider": "PAYPAL",
                  "productCode": "ANNUAL"
                }
            """.trimIndent()
        }.andExpect {
            content {
                """
                {
                  "productSnapshot": {
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
                  "status": "ACTIVE"
                }""".let { json(it) }
            }
        }
    }
}
