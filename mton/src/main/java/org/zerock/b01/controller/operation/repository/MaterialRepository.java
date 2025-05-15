package org.zerock.b01.controller.operation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.zerock.b01.domain.operation.Material;

import java.util.List;

public interface MaterialRepository extends JpaRepository<Material, String> {
    @Query(value = "SELECT m.matId FROM Material m WHERE m.matId LIKE ?1% ORDER BY m.matId DESC LIMIT 1")
    String findLastOrderIdByPrefix(String prefix);

    // 구매 및 발주 > 계약정보 : 자재코드로 자재명 불러오기
    @Query("SELECT m.matName FROM Material m WHERE m.matId IN :codes")
    List<String> findNamesByMatId(@Param("codes") List<String> codes);
}
