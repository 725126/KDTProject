package org.zerock.b01.repository.search.warehouse;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.zerock.b01.domain.warehouse.Outgoing;

import java.time.LocalDate;

public interface OutgoingSearch {

  Page<Outgoing> searchOutgoing(LocalDate outgoingCompletedAtStart, LocalDate outgoingCompletedAtEnd,
                                String outgoingCode, String matId, String matName,
                                Pageable pageable);
}
