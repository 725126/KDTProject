package org.zerock.b01.service.warehouse;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.zerock.b01.domain.warehouse.DeliveryPartnerItem;
import org.zerock.b01.domain.warehouse.Incoming;
import org.zerock.b01.dto.PageRequestDTO;
import org.zerock.b01.dto.PageResponseDTO;
import org.zerock.b01.dto.warehouse.IncomingDTO;

import java.time.LocalDate;

public interface IncomingService {

  String generateIncomingCode();

  void createIncomingForDeliveryPartnerItem(DeliveryPartnerItem deliveryPartnerItem);

  PageResponseDTO<IncomingDTO> listWithIncoming(PageRequestDTO pageRequestDTO);

}
