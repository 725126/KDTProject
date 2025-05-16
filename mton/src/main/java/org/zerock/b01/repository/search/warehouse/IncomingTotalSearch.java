package org.zerock.b01.repository.search.warehouse;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.zerock.b01.domain.warehouse.IncomingTotal;

import java.time.LocalDate;

public interface IncomingTotalSearch {

  Page<IncomingTotal> searchIncomingTotal(LocalDate incomingCompletedAtStart,
                                          LocalDate incomingCompletedAtEnd, String pCompany,
                                          String matId, String matName,
                                          Pageable pageable);
}
