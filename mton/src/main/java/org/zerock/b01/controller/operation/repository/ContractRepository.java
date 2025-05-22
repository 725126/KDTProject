package org.zerock.b01.controller.operation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.zerock.b01.domain.operation.Contract;
import org.zerock.b01.domain.user.Partner;

import java.time.LocalDate;
import java.util.List;

public interface ContractRepository extends JpaRepository<Contract, Long> {
    @Query("SELECT COUNT(c) FROM Contract c " +
            "WHERE c.partner = :partner " +
            "AND c.conDate <= :today " +
            "AND c.conEnd >= :today")
    int countOngoingContracts(@Param("partner") Partner partner, @Param("today") LocalDate today);

    // 계약 종료 임박 (conEnd ≤ 오늘 + 7일)
    @Query("""
        SELECT c FROM Contract c
        WHERE c.partner = :partner
        AND c.conEnd BETWEEN :today AND :sevenDaysLater
    """)
    List<Contract> findExpiringContracts(@Param("partner") Partner partner,
                                         @Param("today") LocalDate today,
                                         @Param("sevenDaysLater") LocalDate sevenDaysLater);

}
