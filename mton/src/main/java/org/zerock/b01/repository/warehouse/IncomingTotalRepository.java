package org.zerock.b01.repository.warehouse;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.zerock.b01.domain.operation.Ordering;
import org.zerock.b01.domain.user.Partner;
import org.zerock.b01.domain.warehouse.DeliveryRequestItem;
import org.zerock.b01.domain.warehouse.IncomingStatus;
import org.zerock.b01.domain.warehouse.IncomingTotal;
import org.zerock.b01.repository.search.warehouse.IncomingTotalSearch;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface IncomingTotalRepository extends JpaRepository<IncomingTotal, Long>, IncomingTotalSearch {

  Optional<IncomingTotal> findByDeliveryRequestItem(DeliveryRequestItem deliveryRequestItem);

  Optional<IncomingTotal> findByDeliveryRequestItem_drItemId(Long drItemId);

  @Query("""
    SELECT CASE WHEN COUNT(it) > 0 THEN true ELSE false END
    FROM IncomingTotal it
    JOIN it.deliveryRequestItem dri
    JOIN dri.deliveryRequest dr
    JOIN dr.ordering oi
    WHERE oi.orderId = :orderId
      AND it.incomingStatus != :status
""")
  boolean existsByOrderIdAndStatusNot(@Param("orderId") String orderId,
                                      @Param("status") IncomingStatus status);

  @Query("SELECT MAX(it.incomingCompletedAt) FROM IncomingTotal it " +
          "JOIN it.deliveryRequestItem dri " +
          "JOIN dri.deliveryRequest dr " +
          "JOIN dr.ordering o " +
          "WHERE o.orderId = :orderId AND o.orderStat = '완료'")
  LocalDateTime findLatestIncomingCompletedAtByOrderIdAndOrderStatCompleted(@Param("orderId") String orderId);

  @Query("SELECT cm.material.matId, SUM(it.incomingEffectiveQty) " +
          "FROM IncomingTotal it " +
          "JOIN it.deliveryRequestItem dri " +
          "JOIN dri.deliveryRequest dr " +
          "JOIN dr.ordering o " +
          "JOIN o.contractMaterial cm " +
          "WHERE it.incomingStatus != '입고마감' ")
  List<Object[]> sumUnclosedIncomingQtyByMaterial();

  List<IncomingTotal> findByIncomingStatus(IncomingStatus status);

  // 전체 입고완료 건수
  @Query("""
    SELECT COUNT(it) 
    FROM IncomingTotal it 
    WHERE it.incomingStatus = '입고마감'
    AND it.deliveryRequestItem.deliveryRequest.ordering.contractMaterial.contract.partner = :partner
    """)
  long countCompletedIncomingByPartner(@Param("partner") Partner partner);

  // 납기 준수 입고완료 건수
  @Query("""
    SELECT COUNT(it) 
    FROM IncomingTotal it 
    WHERE it.incomingStatus = '입고마감'
    AND it.incomingCompletedAt IS NOT NULL
    AND it.deliveryRequestItem.drItemDueDate >= FUNCTION('DATE', it.incomingCompletedAt)
    AND it.deliveryRequestItem.deliveryRequest.ordering.contractMaterial.contract.partner = :partner
    """)
  long countOnTimeIncomingByPartner(@Param("partner") Partner partner);

  @Query("""
    SELECT 
        FUNCTION('MONTH', it.incomingCompletedAt),
        SUM(CASE WHEN it.deliveryRequestItem.drItemDueDate >= FUNCTION('DATE', it.incomingCompletedAt) THEN 1 ELSE 0 END),
        SUM(CASE WHEN it.deliveryRequestItem.drItemDueDate < FUNCTION('DATE', it.incomingCompletedAt) THEN 1 ELSE 0 END)
    FROM IncomingTotal it
    WHERE it.incomingStatus = '입고마감'
    AND FUNCTION('YEAR', it.incomingCompletedAt) = :year
    AND it.deliveryRequestItem.deliveryRequest.ordering.contractMaterial.contract.partner = :partner
    GROUP BY FUNCTION('MONTH', it.incomingCompletedAt)
    ORDER BY FUNCTION('MONTH', it.incomingCompletedAt)
""")
  List<Object[]> getMonthlyOnTimeAndDelayedCount(@Param("partner") Partner partner, @Param("year") int year);

  @Query("""
    SELECT 
        FUNCTION('MONTH', it.incomingCompletedAt),
        COUNT(it),
        SUM(CASE WHEN it.deliveryRequestItem.drItemDueDate >= FUNCTION('DATE', it.incomingCompletedAt) THEN 1 ELSE 0 END)
    FROM IncomingTotal it
    WHERE it.incomingStatus = '입고마감'
    AND FUNCTION('YEAR', it.incomingCompletedAt) = :year
    AND it.deliveryRequestItem.deliveryRequest.ordering.contractMaterial.contract.partner = :partner
    GROUP BY FUNCTION('MONTH', it.incomingCompletedAt)
    ORDER BY FUNCTION('MONTH', it.incomingCompletedAt)
""")
  List<Object[]> getMonthlyOnTimeRateData(@Param("partner") Partner partner, @Param("year") int year);

  @Query("""
    SELECT i FROM IncomingTotal i
    WHERE i.incomingStatus = '입고마감'
    AND i.deliveryRequestItem.deliveryRequest.ordering.contractMaterial.contract.partner = :partner
""")
  List<IncomingTotal> findCompletedIncomingByPartner(@Param("partner") Partner partner);

}
