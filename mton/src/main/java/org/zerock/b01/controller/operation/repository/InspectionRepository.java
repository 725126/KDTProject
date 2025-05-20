package org.zerock.b01.controller.operation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.zerock.b01.domain.operation.Inspection;

import java.util.List;

public interface InspectionRepository extends JpaRepository<Inspection, String> {
    @Query(value = "SELECT i.insId FROM Inspection i WHERE i.insId LIKE ?1% ORDER BY i.insId DESC LIMIT 1")
    String findLastOrderIdByPrefix(String prefix);

    @Query(value = "SELECT i FROM Inspection i WHERE i.ordering.orderId IN :orderIds")
    List<Inspection> findByOrderIds(@Param("orderIds") List<String> orderIds);
}
