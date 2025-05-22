package org.zerock.b01.controller.warehouse;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zerock.b01.domain.warehouse.DeliveryPartner;
import org.zerock.b01.domain.warehouse.OutgoingTotal;
import org.zerock.b01.dto.warehouse.OutgoingDTO;
import org.zerock.b01.repository.warehouse.OutgoingTotalRepository;
import org.zerock.b01.service.warehouse.OutgoingService;
import org.zerock.b01.service.warehouse.OutgoingTotalService;

import java.util.List;
import java.util.Map;

@RestController
@Log4j2
@RequestMapping("/api/outgoing")
@RequiredArgsConstructor
public class OutgoingRestController {

  private final OutgoingService outgoingService;
  private final OutgoingTotalService outgoingTotalService;
  private final OutgoingTotalRepository outgoingTotalRepository;

  // 부분출고
  @PostMapping("/partial")
  public ResponseEntity<?> partialOutgoing(@RequestBody OutgoingDTO dto) {
    outgoingService.partialOutgoing(dto);
    return ResponseEntity.ok("부분입고 완료");
  }

  // 전체출고
  @PostMapping("/full")
  public ResponseEntity<?> fullOutgoing(@RequestBody List<OutgoingDTO> dtoList) {
    outgoingService.fullOutgoing(dtoList);
    return ResponseEntity.ok("전체입고 완료");
  }

  //출고마감
  @PostMapping("/close")
  public ResponseEntity<?> closeIncoming(@RequestBody Map<String, Long> body) {
    Long outgoingTotalId = body.get("outgoingTotalId");

    OutgoingTotal outgoingTotal = outgoingTotalRepository.findById(outgoingTotalId)
            .orElseThrow(() -> new IllegalArgumentException("해당 납입지시 항목이 존재하지 않습니다."));

    // 서비스 호출로 입고 마감 처리
    outgoingTotalService.closeOutgoing(outgoingTotalId);

    return ResponseEntity.ok().build();
  }
}
