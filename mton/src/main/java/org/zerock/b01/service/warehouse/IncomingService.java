package org.zerock.b01.service.warehouse;

import org.zerock.b01.domain.warehouse.DeliveryPartnerItem;
import org.zerock.b01.dto.PageRequestDTO;
import org.zerock.b01.dto.PageResponseDTO;
import org.zerock.b01.dto.warehouse.IncomingDTO;
import org.zerock.b01.dto.warehouse.IncomingInspectionDTO;

import java.util.List;

public interface IncomingService {

  String generateIncomingCode();

  void createIncomingForDeliveryPartnerItem(DeliveryPartnerItem deliveryPartnerItem);

  PageResponseDTO<IncomingDTO> listWithIncoming(PageRequestDTO pageRequestDTO);

  PageResponseDTO<IncomingInspectionDTO> listWithIncomingInspection(PageRequestDTO pageRequestDTO);

  void partialIncoming(IncomingInspectionDTO dto);

  void fullIncoming(List<IncomingInspectionDTO> dtoList);

  void modifyIncoming(IncomingInspectionDTO dto);

  void returnIncoming(IncomingInspectionDTO dto);

  PageResponseDTO<IncomingInspectionDTO> getListIncomingWithTotal(Long incomingTotalId,
                                                                  PageRequestDTO pageRequestDTO);

}
