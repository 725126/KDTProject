package org.zerock.b01.controller.operation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.zerock.b01.domain.operation.Ordering;

import java.util.List;

public interface OrderingRepository extends JpaRepository<Ordering, String> {
    @Query(value = "SELECT o.orderId FROM Ordering o WHERE o.orderId LIKE ?1% ORDER BY o.orderId DESC LIMIT 1")
    String findLastOrderIdByPrefix(String prefix);

    @Query(value = "SELECT o FROM Ordering o WHERE o.contractMaterial.contract.partner.user.userId = :userId AND o.orderStat IN ('진행중', '발주중') ORDER BY o.orderId DESC")
    List<Ordering> findOrderingByUserId(@Param("userId") Long user);

    @Query(value = "SELECT o FROM Ordering o WHERE o.contractMaterial.contract.partner.user.userId = :userId AND o.orderStat = '취소대기' ORDER BY o.orderId DESC")
    List<Ordering> findCancelOrderingByUserId(@Param("userId") Long user);

    @Query(value = "SELECT o FROM Ordering o WHERE o.procurementPlan.pplanId IN :pplanIds")
    List<Ordering> findOrderingByPPlanId(@Param("pplanIds") List<String> pplanIds);
}
