package org.zerock.b01.repository.search.warehouse;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.zerock.b01.domain.warehouse.DeliveryRequest;

import java.time.LocalDate;

public interface DeliveryRequestSearch {

  Page<DeliveryRequest> searchDeliveryRequestAll(String orderId, String matName,
                                                 LocalDate orderDateStart, LocalDate orderDateEnd,
                                                 LocalDate orderEndStart, LocalDate orderEndEnd,
                                                 Pageable pageable);

}
