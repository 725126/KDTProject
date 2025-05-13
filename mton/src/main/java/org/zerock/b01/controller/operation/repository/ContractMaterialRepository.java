package org.zerock.b01.controller.operation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.zerock.b01.domain.operation.ContractMaterial;

public interface ContractMaterialRepository extends JpaRepository<ContractMaterial, String> {
    @Query(value = "SELECT c.cmtId FROM ContractMaterial c WHERE c.cmtId LIKE ?1% ORDER BY c.cmtId DESC LIMIT 1")
    String findLastOrderIdByPrefix(String prefix);
}
