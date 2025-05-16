package org.zerock.b01.service.warehouse;

import org.zerock.b01.domain.warehouse.CompanyStorage;
import org.zerock.b01.domain.warehouse.DeliveryRequest;
import org.zerock.b01.dto.PageRequestDTO;
import org.zerock.b01.dto.PageResponseDTO;
import org.zerock.b01.dto.warehouse.DeliveryRequestDTO;

import java.util.List;

public interface DeliveryRequestService {

  PageResponseDTO<DeliveryRequestDTO> listWithDeliveryRequest(PageRequestDTO pageRequestDTO);

  List<CompanyStorage> getCompanyStorageList();

  DeliveryRequestDTO readDeliveryRequestOne(String orderId);

  void updateDeliveryRequestStatus(Long drId);

  void createDeliveryRequestFromOrdering(String orderId);

  default DeliveryRequestDTO entityToDto(DeliveryRequest deliveryRequest) {

    DeliveryRequestDTO dto = DeliveryRequestDTO.builder()
            .drId(deliveryRequest.getDrId())
            .orderId(deliveryRequest.getOrdering().getOrderId())
            .matName(deliveryRequest.getOrdering().getContractMaterial().getMaterial().getMatName())
            .orderDate(deliveryRequest.getOrdering().getOrderDate())
            .orderEnd(deliveryRequest.getOrdering().getOrderEnd())
            .orderQty(deliveryRequest.getOrdering().getOrderQty())
            .drTotalQty(deliveryRequest.getDrTotalQty())
            .drStatus(deliveryRequest.getDrStatus().name())
            .build();

    return dto;

  }



}

