package org.zerock.b01.controller.operation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.zerock.b01.domain.operation.Product;

public interface ProductRepository extends JpaRepository<Product, String> {
    @Query(value = "SELECT p.prodId FROM Product p WHERE p.prodId LIKE ?1% ORDER BY p.prodId DESC LIMIT 1")
    String findLastOrderIdByPrefix(String prefix);
}
