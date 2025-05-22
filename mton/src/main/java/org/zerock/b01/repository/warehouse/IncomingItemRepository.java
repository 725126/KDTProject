package org.zerock.b01.repository.warehouse;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.zerock.b01.domain.user.Partner;
import org.zerock.b01.domain.warehouse.IncomingItem;
import org.zerock.b01.domain.warehouse.IncomingTotal;
import org.zerock.b01.repository.search.warehouse.IncomingItemSearch;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface IncomingItemRepository extends JpaRepository<IncomingItem,Long>, IncomingItemSearch {

  List<IncomingItem> findByIncoming_IncomingTotal(IncomingTotal incomingTotal);

  @Query("SELECT FUNCTION('DAYOFWEEK', i.modifyDate) as dow, SUM(i.incomingQty - i.incomingReturnQty) " +
          "FROM IncomingItem i " +
          "WHERE i.modifyDate BETWEEN :start AND :end " +
          "GROUP BY FUNCTION('DAYOFWEEK', i.modifyDate)")
  List<Object[]> getWeeklyIncomingSummary(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);



}
