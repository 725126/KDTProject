package org.zerock.b01.repository.warehouse;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.zerock.b01.domain.operation.Ordering;
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
}
