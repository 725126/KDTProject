package org.zerock.b01.service.warehouse;

import org.zerock.b01.dto.PageRequestDTO;
import org.zerock.b01.dto.PageResponseDTO;
import org.zerock.b01.dto.warehouse.DeliveryRequestItemDTO;

import java.time.LocalDate;
import java.util.List;

public interface DeliveryRequestItemService {

  String generateDrItemCode();

  List<DeliveryRequestItemDTO> registerDeliveryRequestItem(List<DeliveryRequestItemDTO> dtoList);

  DeliveryRequestItemDTO readDeliveryRequestItem(Long drItemId);

  void modifyDeliveryRequestItem(DeliveryRequestItemDTO deliveryRequestItemDTO);

  void removeDeliveryRequestItem(Long drItemId);

  PageResponseDTO<DeliveryRequestItemDTO> getListDeliveryRequestItem(Long drId,
                                                                     PageRequestDTO pageRequestDTO);
  LocalDate getLastDrItemDueDate(Long drItemId);

  PageResponseDTO<DeliveryRequestItemDTO> listWithDeliveryRequestItem(PageRequestDTO pageRequestDTO);

}
