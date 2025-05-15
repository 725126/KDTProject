package org.zerock.b01.controller.operation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.zerock.b01.domain.operation.ProcurementPlan;

import java.util.List;

public interface ProcurementPlanRepository extends JpaRepository<ProcurementPlan, String> {
    @Query(value = "SELECT p.pplanId FROM ProcurementPlan p WHERE p.pplanId LIKE ?1% ORDER BY p.pplanId DESC LIMIT 1")
    String findLastOrderIdByPrefix(String prefix);

    @Query(value = "SELECT p FROM ProcurementPlan p WHERE p.prdplan.prdplanId = :prdplanId AND p.material.matId = :matId")
    List<ProcurementPlan> findDuplicatedPPlan(@Param("prdplanId") String prdplanId, @Param("matId") String matId);
}
