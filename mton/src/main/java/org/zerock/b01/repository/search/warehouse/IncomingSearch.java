package org.zerock.b01.repository.search.warehouse;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.zerock.b01.domain.warehouse.Incoming;

import java.time.LocalDate;

public interface IncomingSearch {

  Page<Incoming> searchIncoming(LocalDate deliveryPartnerItemDateStart,
                                LocalDate deliveryPartnerItemDateEnd,
                                String incomingCode, String pCompany,
                                String matId, String matName,
                                Pageable pageable);

  Page<Incoming> searchIncomingInspection(LocalDate deliveryPartnerItemDateStart,
                                          LocalDate deliveryPartnerItemDateEnd, String incomingCode,
                                          String pCompany, String matId, String matName,
                                          LocalDate incomingFirstDateStart, LocalDate incomingFirstDateEnd,
                                          String incomingStatus, Pageable pageable);

}
