package org.zerock.b01.controller.warehouse;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zerock.b01.dto.warehouse.IncomingInspectionDTO;
import org.zerock.b01.service.warehouse.IncomingService;

import java.util.List;

@RestController
@Log4j2
@RequestMapping("/api/inspection")
@RequiredArgsConstructor
public class IncomingInspectionController {

  private final IncomingService incomingService;

  // 부분출고
  @PostMapping("/partial")
  public ResponseEntity<?> partialDelivery(@RequestBody IncomingInspectionDTO dto) {
    incomingService.partialIncoming(dto);
    return ResponseEntity.ok("부분출고 완료");
  }

  // 전체출고
  @PostMapping("/full")
  public ResponseEntity<?> fullDelivery(@RequestBody List<IncomingInspectionDTO> dtoList) {
    incomingService.fullIncoming(dtoList);
    return ResponseEntity.ok("전체출고 완료");
  }


}
