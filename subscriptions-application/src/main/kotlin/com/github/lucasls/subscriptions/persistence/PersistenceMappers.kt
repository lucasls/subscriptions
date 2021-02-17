package com.github.lucasls.subscriptions.persistence

import com.github.lucasls.subscriptions.DomainProduct
import com.github.lucasls.subscriptions.DomainSubscription
import com.github.lucasls.subscriptions.persistence.jpa.entity.Price
import com.github.lucasls.subscriptions.persistence.jpa.entity.Product
import com.github.lucasls.subscriptions.persistence.jpa.entity.ProductSnapshot
import com.github.lucasls.subscriptions.persistence.jpa.entity.Subscription
import org.joda.money.CurrencyUnit
import org.joda.money.Money
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Mappings
import java.util.UUID

@Mapper(
    imports = [java.time.Period::class],
    uses = [PersistenceMappers.CustomMappers::class]
)
interface PersistenceMappers {
    // Product Annotations ---

    @Mapping(target = "subscriptionPeriod", expression = "java(Period.ofMonths(p.getSubscriptionPeriodMonths()))")
    annotation class MappingProductToDomain

    @Mapping(target = "subscriptionPeriodMonths", source = "subscriptionPeriod.months")
    annotation class MappingProductFromDomain

    // From domain ---

    @Mappings(
        Mapping(source = "amount", target = "value"),
        Mapping(source = "currencyUnit.code", target = "unit"),
    )
    fun fromDomain(m: Money): Price

    @Mapping(target = "subscriptionPeriodMonths", source = "subscriptionPeriod.months")
    fun fromDomainToProduct(p: DomainProduct): Product

    @MappingProductFromDomain
    fun fromDomainToProductSnapshot(p: DomainProduct): ProductSnapshot

    fun fromDomain(userId: UUID, s: DomainSubscription): Subscription

    // To domain ---

    @MappingProductToDomain
    fun toDomain(p: Product): DomainProduct

    @MappingProductToDomain
    fun toDomain(p: ProductSnapshot): DomainProduct

    @Mapping(target = "withStatus", ignore = true)
    fun toDomain(s: Subscription): DomainSubscription

    // Other ---
    
    class CustomMappers {
        fun Price.toDomain(): Money = Money.of(
            CurrencyUnit.of(unit.toUpperCase()),
            value
        )
    }

    companion object {
        val instance = PersistenceMappersImpl()
        fun Product.toDomain() = instance.toDomain(this)
        fun Subscription.toDomain() = instance.toDomain(this)
        fun Pair<UUID, DomainSubscription>.fromDomain() = instance.fromDomain(this.first, this.second)
    }
}