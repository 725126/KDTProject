package org.zerock.b01.repository.warehouse;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.zerock.b01.domain.user.Partner;
import org.zerock.b01.domain.warehouse.OutgoingStatus;
import org.zerock.b01.domain.warehouse.OutgoingTotal;
import org.zerock.b01.repository.search.warehouse.OutgoingTotalSearch;

import java.util.List;

public interface OutgoingTotalRepository extends JpaRepository<OutgoingTotal, Long>, OutgoingTotalSearch {

  List<OutgoingTotal> findAllByProductionPlanPrdplanId(String prdplanId);

  @Query("SELECT ot.material.matId, SUM(ot.outgoingTotalQty) " +
          "FROM OutgoingTotal ot " +
          "WHERE ot.outgoingStatus != '출고마감' " +
          "GROUP BY ot.material.matId")
  List<Object[]> sumUnclosedOutgoingTotalQtyByMaterial();

  List<OutgoingTotal> findByOutgoingStatus(OutgoingStatus status);

}
