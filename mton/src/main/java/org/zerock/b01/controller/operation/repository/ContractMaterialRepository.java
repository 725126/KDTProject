package org.zerock.b01.controller.operation.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.zerock.b01.domain.operation.ContractMaterial;

import java.util.List;

public interface ContractMaterialRepository extends JpaRepository<ContractMaterial, Long> {
    @Query("SELECT cm FROM ContractMaterial cm " +
            "JOIN FETCH cm.contract c " +
            "JOIN FETCH c.partner p " +
            "LEFT JOIN FETCH cm.material m")
    List<ContractMaterial> findAllWithContractAndMaterial();

    @Query("SELECT cm FROM ContractMaterial cm " +
            "JOIN FETCH cm.contract c " +
            "JOIN FETCH c.partner p " +
            "LEFT JOIN FETCH cm.material m " +
            "WHERE (:keyword IS NULL OR " +
            "LOWER(p.pCompany) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(m.matName) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<ContractMaterial> searchContractMaterials(@Param("keyword") String keyword, Pageable pageable);


}
