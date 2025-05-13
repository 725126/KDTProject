package org.zerock.b01.controller.operation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.zerock.b01.domain.operation.ProcurementPlan;

public interface ProcurementPlanRepository extends JpaRepository<ProcurementPlan, String> {
    @Query(value = "SELECT p.pplanId FROM ProcurementPlan p WHERE p.pplanId LIKE ?1% ORDER BY p.pplanId DESC LIMIT 1")
    String findLastOrderIdByPrefix(String prefix);
}
