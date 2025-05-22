package org.zerock.b01.repository.warehouse;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.zerock.b01.domain.warehouse.Outgoing;
import org.zerock.b01.repository.search.warehouse.OutgoingSearch;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OutgoingRepository extends JpaRepository<Outgoing, Long>, OutgoingSearch {

  @Query("SELECT o.outgoingCode FROM Outgoing o " +
          "WHERE o.outgoingCode LIKE CONCAT(:prefix, '%') " +
          "ORDER BY o.outgoingCode DESC")
  List<String> findOutgoingTopByPrefix(@Param("prefix") String prefix, Pageable pageable);

  List<Outgoing> findByOutgoingTotalOutgoingTotalId(Long outgoingTotalId);

  @Query("SELECT SUM(o.outgoingQty) " +
          "FROM Outgoing o " +
          "WHERE o.inventory.inventoryId = :inventoryId ")
  Optional<Integer> sumUnclosedOutgoingQtyByInventoryId(@Param("inventoryId") Long inventoryId);

  @Query("SELECT FUNCTION('DAYOFWEEK', o.outgoingDate) as dow, SUM(o.outgoingQty) " +
          "FROM Outgoing o " +
          "WHERE o.outgoingDate BETWEEN :start AND :end " +
          "GROUP BY FUNCTION('DAYOFWEEK', o.outgoingDate)")
  List<Object[]> getWeeklyOutgoingSummary(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

}
