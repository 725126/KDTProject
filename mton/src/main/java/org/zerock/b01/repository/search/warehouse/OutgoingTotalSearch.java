package org.zerock.b01.repository.search.warehouse;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.zerock.b01.domain.warehouse.OutgoingTotal;

import java.time.LocalDate;

public interface OutgoingTotalSearch {

  Page<OutgoingTotal> searchOutgoingTotal(LocalDate prdplanEndStart, LocalDate prdplanEndEnd,
                                          String prdplanId, String matId, String matName,
                                          String outgoingStatus, LocalDate outgoingCompletedAtStart,
                                          LocalDate outgoingCompletedAtEnd, Pageable pageable);
}
