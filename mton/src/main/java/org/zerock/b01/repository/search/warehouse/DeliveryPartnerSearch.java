package org.zerock.b01.repository.search.warehouse;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.zerock.b01.domain.warehouse.DeliveryPartner;

import java.time.LocalDate;

public interface DeliveryPartnerSearch {

  Page<DeliveryPartner> searchDeliveryPartnerAll(String drItemCode, String orderId, String matName,
                                                 LocalDate drItemDueDateStart, LocalDate drItemDueDateEnd,
                                                 Pageable pageable);
}
