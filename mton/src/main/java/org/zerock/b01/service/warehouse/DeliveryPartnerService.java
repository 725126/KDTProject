package org.zerock.b01.service.warehouse;

import org.zerock.b01.dto.PageRequestDTO;
import org.zerock.b01.dto.PageResponseDTO;
import org.zerock.b01.dto.warehouse.DeliveryPartnerDTO;

import java.util.List;

public interface DeliveryPartnerService {

  PageResponseDTO<DeliveryPartnerDTO> listWithDeliveryPartner(PageRequestDTO pageRequestDTO);

  void partialDelivery(DeliveryPartnerDTO dto);

  void fullDelivery(List<DeliveryPartnerDTO> dtoList);
}
