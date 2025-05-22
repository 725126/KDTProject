package org.zerock.b01.controller.operation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.zerock.b01.domain.operation.Inspection;
import org.zerock.b01.domain.user.Partner;

import java.util.List;

public interface InspectionRepository extends JpaRepository<Inspection, String> {
    @Query(value = "SELECT i.insId FROM Inspection i WHERE i.insId LIKE ?1% ORDER BY i.insId DESC LIMIT 1")
    String findLastOrderIdByPrefix(String prefix);

    @Query(value = "SELECT i FROM Inspection i WHERE i.ordering.orderId IN :orderIds")
    List<Inspection> findByOrderIds(@Param("orderIds") List<String> orderIds);

    // 전체 진척검수 건수
    @Query("""
    SELECT COUNT(i) FROM Inspection i
    WHERE i.ordering.contractMaterial.contract.partner = :partner
    """)
    long countAllInspections(@Param("partner") Partner partner);

    // 완료된 진척검수 건수
    @Query("""
    SELECT COUNT(i) FROM Inspection i
    WHERE i.insStat = '완료'
    AND i.ordering.contractMaterial.contract.partner = :partner
    """)
    long countCompletedInspections(@Param("partner") Partner partner);
}
