package org.zerock.b01.controller.partner;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.zerock.b01.domain.operation.PartnerStorage;
import org.zerock.b01.dto.warehouse.DeliveryPartnerDTO;
import org.zerock.b01.dto.warehouse.PartnerStorageDTO;
import org.zerock.b01.service.warehouse.DeliveryPartnerService;
import org.zerock.b01.service.warehouse.PartnerStorageService;

import java.util.List;

@RestController
@Log4j2
@RequestMapping("/api/partner")
@RequiredArgsConstructor
public class PartnerRestController {

  private final DeliveryPartnerService deliveryPartnerService;
  private final PartnerStorageService partnerStorageService;

  // 부분출고
  @PostMapping("/partial")
  public ResponseEntity<?> partialDelivery(@RequestBody DeliveryPartnerDTO dto) {
    deliveryPartnerService.partialDelivery(dto);
    return ResponseEntity.ok("부분출하 완료");
  }

  // 전체출고
  @PostMapping("/full")
  public ResponseEntity<?> fullDelivery(@RequestBody List<DeliveryPartnerDTO> dtoList) {
    deliveryPartnerService.fullDelivery(dtoList);
    return ResponseEntity.ok("전체출하 완료");
  }

  @PostMapping("/storageModify")
  public ResponseEntity<?> storageModify(@RequestBody List<PartnerStorageDTO> dtoList) {
    partnerStorageService.modifyStorage(dtoList);
    return ResponseEntity.ok("변경 완료");
  }
}
