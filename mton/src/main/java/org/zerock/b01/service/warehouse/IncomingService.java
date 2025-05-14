package org.zerock.b01.service.warehouse;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.zerock.b01.domain.warehouse.DeliveryPartnerItem;
import org.zerock.b01.domain.warehouse.Incoming;
import org.zerock.b01.dto.PageRequestDTO;
import org.zerock.b01.dto.PageResponseDTO;
import org.zerock.b01.dto.warehouse.IncomingDTO;
import org.zerock.b01.dto.warehouse.IncomingInspectionDTO;

import java.time.LocalDate;
import java.util.List;

public interface IncomingService {

  String generateIncomingCode();

  void createIncomingForDeliveryPartnerItem(DeliveryPartnerItem deliveryPartnerItem);

  PageResponseDTO<IncomingDTO> listWithIncoming(PageRequestDTO pageRequestDTO);

  PageResponseDTO<IncomingInspectionDTO> listWithIncomingInspection(PageRequestDTO pageRequestDTO);

  void partialIncoming(IncomingInspectionDTO dto);

  void fullIncoming(List<IncomingInspectionDTO> dtoList);

  void returnItems(IncomingInspectionDTO dto);

}
