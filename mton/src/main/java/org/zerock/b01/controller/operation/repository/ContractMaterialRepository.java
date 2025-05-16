package org.zerock.b01.controller.operation.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.zerock.b01.domain.operation.ContractMaterial;

import java.util.List;

public interface ContractMaterialRepository extends JpaRepository<ContractMaterial, String> {

    // 전체 계약자재 조회 (계약 + 협력업체 + 자재 정보 포함)
    @Query("SELECT cm FROM ContractMaterial cm " +
            "JOIN FETCH cm.contract c " +
            "JOIN FETCH c.partner p " +
            "LEFT JOIN FETCH cm.material m")
    List<ContractMaterial> findAllWithContractAndMaterial();

    // 키워드 검색 + 페이지네이션 지원
    @Query("SELECT cm FROM ContractMaterial cm " +
            "JOIN FETCH cm.contract c " +
            "JOIN FETCH c.partner p " +
            "LEFT JOIN FETCH cm.material m " +
            "WHERE (:keyword IS NULL OR " +
            "LOWER(p.pCompany) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(m.matName) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<ContractMaterial> searchContractMaterials(@Param("keyword") String keyword, Pageable pageable);

    // 마지막 계약자재코드 찾기 (자동 생성용)
    @Query("SELECT c.cmtId FROM ContractMaterial c WHERE c.cmtId LIKE ?1% ORDER BY c.cmtId DESC LIMIT 1")
    String findLastOrderIdByPrefix(String prefix);
}
