package org.zerock.b01.repository.warehouse;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.b01.domain.warehouse.OutgoingTotal;
import org.zerock.b01.repository.search.warehouse.OutgoingTotalSearch;

import java.util.List;

public interface OutgoingTotalRepository extends JpaRepository<OutgoingTotal, Long>, OutgoingTotalSearch {

  List<OutgoingTotal> findAllByProductionPlanPrdplanId(String prdplanId);

}
