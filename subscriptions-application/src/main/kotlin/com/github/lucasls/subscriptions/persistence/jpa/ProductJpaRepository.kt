package com.github.lucasls.subscriptions.persistence.jpa

import com.github.lucasls.subscriptions.persistence.jpa.entity.Product
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ProductJpaRepository : CrudRepository<Product, String>