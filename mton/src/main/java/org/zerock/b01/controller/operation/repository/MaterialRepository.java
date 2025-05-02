package org.zerock.b01.controller.operation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.zerock.b01.domain.operation.Material;

public interface MaterialRepository extends JpaRepository<Material, String> {
    @Query(value = "SELECT m.matId FROM Material m WHERE m.matId LIKE ?1% ORDER BY m.matId DESC LIMIT 1")
    String findLastOrderIdByPrefix(String prefix);
}
