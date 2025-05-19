package org.zerock.b01.controller.operation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.zerock.b01.domain.operation.Inspection;

public interface InspectionRepository extends JpaRepository<Inspection, String> {
    @Query(value = "SELECT i.insId FROM Inspection i WHERE i.insId LIKE ?1% ORDER BY i.insId DESC LIMIT 1")
    String findLastOrderIdByPrefix(String prefix);
}
