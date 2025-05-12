package org.zerock.b01.repository.search.warehouse;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.zerock.b01.domain.warehouse.DeliveryRequestItem;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface DeliveryRequestItemSearch {

  Page<DeliveryRequestItem> searchDeliveryRequestItemAll(String drItemCode, String orderId, String matName,
                                                         LocalDate orderEndStart, LocalDate orderEndEnd,
                                                         LocalDate drItemDueDateStart, LocalDate drItemDueDateEnd,
                                                         LocalDate creDateStart, LocalDate creDateEnd,
                                                         Pageable pageable);
}
