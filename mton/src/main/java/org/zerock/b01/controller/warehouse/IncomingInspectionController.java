package org.zerock.b01.controller.warehouse;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zerock.b01.domain.warehouse.DeliveryPartner;
import org.zerock.b01.domain.warehouse.IncomingTotal;
import org.zerock.b01.dto.warehouse.IncomingInspectionDTO;
import org.zerock.b01.repository.warehouse.DeliveryPartnerRepository;
import org.zerock.b01.repository.warehouse.IncomingTotalRepository;
import org.zerock.b01.service.warehouse.DeliveryPartnerService;
import org.zerock.b01.service.warehouse.IncomingService;
import org.zerock.b01.service.warehouse.IncomingTotalService;

import java.util.List;
import java.util.Map;

@RestController
@Log4j2
@RequestMapping("/api/inspection")
@RequiredArgsConstructor
public class IncomingInspectionController {

  private final IncomingService incomingService;
  private final IncomingTotalService incomingTotalService;
  private final IncomingTotalRepository incomingTotalRepository;
  private final DeliveryPartnerRepository deliveryPartnerRepository;
  private final DeliveryPartnerService deliveryPartnerService;


  // 부분입고
  @PostMapping("/partial")
  public ResponseEntity<?> partialIncoming(@RequestBody IncomingInspectionDTO dto) {
    incomingService.partialIncoming(dto);
    return ResponseEntity.ok("부분입고 완료");
  }

  // 전체입고
  @PostMapping("/full")
  public ResponseEntity<?> fullIncoming(@RequestBody List<IncomingInspectionDTO> dtoList) {
    incomingService.fullIncoming(dtoList);
    return ResponseEntity.ok("전체입고 완료");
  }

  // 입고 수정
  @PostMapping("/modify")
  public ResponseEntity<?> modifyIncoming(@RequestBody IncomingInspectionDTO dto) {
    incomingService.modifyIncoming(dto);
    return ResponseEntity.ok("입고수정 완료");
  }

  // 반품처리
  @PostMapping("/return")
  public ResponseEntity<?> returnIncoming(@RequestBody IncomingInspectionDTO dto) {
    incomingService.returnIncoming(dto);
    return ResponseEntity.ok("반품처리 완료");
  }

  //입고마감
  @PostMapping("/close")
  public ResponseEntity<?> closeIncoming(@RequestBody Map<String, Long> body) {
    Long incomingTotalId = body.get("incomingTotalId");

    IncomingTotal incomingTotal = incomingTotalRepository.findById(incomingTotalId)
            .orElseThrow(() -> new IllegalArgumentException("해당 납입지시 항목이 존재하지 않습니다."));

    // 서비스 호출로 입고 마감 처리
    incomingTotalService.closeIncoming(incomingTotalId);

    // 2. 해당 입고와 연결된 협력사 출고 정보 가져오기
    DeliveryPartner deliveryPartner = deliveryPartnerRepository.findByIncomingTotal(incomingTotal)
            .orElseThrow(() -> new IllegalStateException("해당 입고와 연결된 협력사 출고 정보가 없습니다."));

    // 3. 출고 상태 업데이트
    deliveryPartnerService.updateDeliveryPartnerStatus(deliveryPartner);

    // 4. 저장
    deliveryPartnerRepository.save(deliveryPartner);

    return ResponseEntity.ok().build();
  }


}
