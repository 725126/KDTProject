package org.zerock.b01.service.warehouse;

import org.zerock.b01.domain.warehouse.DeliveryPartnerItem;
import org.zerock.b01.dto.PageRequestDTO;
import org.zerock.b01.dto.PageResponseDTO;
import org.zerock.b01.dto.warehouse.IncomingDTO;

public interface IncomingService {

  String generateIncomingCode();

  void createIncomingForDeliveryPartnerItem(DeliveryPartnerItem deliveryPartnerItem);

  PageResponseDTO<IncomingDTO> listWithIncoming(PageRequestDTO pageRequestDTO);

}
