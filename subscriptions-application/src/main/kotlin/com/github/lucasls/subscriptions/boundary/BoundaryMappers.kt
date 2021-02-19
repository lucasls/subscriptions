package com.github.lucasls.subscriptions.boundary

import com.github.lucasls.subscriptions.DomainProduct
import com.github.lucasls.subscriptions.DomainSubscription
import com.github.lucasls.subscriptions.boundary.dto.Price
import com.github.lucasls.subscriptions.boundary.dto.Product
import com.github.lucasls.subscriptions.boundary.dto.Subscription
import org.joda.money.Money
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings

@Mapper
interface BoundaryMappers {
    @Mapping(source = "subscriptionPeriod.months", target = "subscriptionPeriodMonths")
    fun fromDomain(p: DomainProduct): Product

    @Mappings(
        Mapping(source = "amount", target = "value"),
        Mapping(source = "currencyUnit.code", target = "unit"),
    )
    fun fromDomain(m: Money): Price

    fun fromDomain(s: DomainSubscription): Subscription

    companion object {
        val instance = BoundaryMappersImpl()
        fun DomainProduct.fromDomain() = instance.fromDomain(this)
        fun Money.fromDomain() = instance.fromDomain(this)
        fun DomainSubscription.fromDomain() = instance.fromDomain(this)
    }
}
