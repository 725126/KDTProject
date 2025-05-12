package org.zerock.b01.controller.operation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.zerock.b01.domain.operation.ProductionPlan;

public interface ProductionPlanRepository extends JpaRepository<ProductionPlan, String> {
    @Query(value = "SELECT p.prdplanId FROM ProductionPlan p WHERE p.prdplanId LIKE ?1% ORDER BY p.prdplanId LIMIT 1")
    String findLastOrderIdByPrefix(String prefix);
}
