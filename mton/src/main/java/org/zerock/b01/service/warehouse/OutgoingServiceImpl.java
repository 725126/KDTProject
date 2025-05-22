package org.zerock.b01.service.warehouse;


import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.zerock.b01.domain.warehouse.*;
import org.zerock.b01.dto.PageRequestDTO;
import org.zerock.b01.dto.PageResponseDTO;
import org.zerock.b01.dto.warehouse.OutgoingDTO;
import org.zerock.b01.repository.warehouse.InventoryRepository;
import org.zerock.b01.repository.warehouse.OutgoingRepository;
import org.zerock.b01.repository.warehouse.OutgoingTotalRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class OutgoingServiceImpl implements OutgoingService {

  private final OutgoingRepository outgoingRepository;
  private final OutgoingTotalRepository outgoingTotalRepository;
  private final OutgoingTotalService outgoingTotalService;
  private final InventoryRepository inventoryRepository;

  public String generateOutgoingCode() {
    String todayPrefix = "OUT-" + java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd")) + "-";
    List<String> result = outgoingRepository.findOutgoingTopByPrefix(todayPrefix, PageRequest.of(0, 1));
    String lastCode = result.isEmpty() ? null : result.get(0);


    int nextNumber = 1;
    if (lastCode != null) {
      String numberPart = lastCode.substring(todayPrefix.length());
      nextNumber = Integer.parseInt(numberPart) + 1;
    }

    return todayPrefix + String.format("%04d", nextNumber);
  }

  // 부분출고
  @Override
  public void partialOutgoing(OutgoingDTO dto) {

    validateOutgoingQty(dto.getOutgoingQty());

    OutgoingTotal outgoingTotal = outgoingTotalRepository.findById(dto.getOutgoingTotalId())
            .orElseThrow(() -> new IllegalArgumentException("해당 항목 없음"));

    int expectedQty = outgoingTotal.getEstimatedOutgoingQty();
    int remainingQty = expectedQty - outgoingTotal.getOutgoingTotalQty();
    int outgoingQty = dto.getOutgoingQty();

    if (remainingQty <= 0) {
      throw new IllegalStateException("남은 출고 수량이 없습니다.");
    }
    if (outgoingQty > remainingQty) {
      throw new IllegalStateException("출고 수량이 남은 수량을 초과합니다.");
    }

    Inventory inventory = inventoryRepository.findById(dto.getInventoryId())
            .orElseThrow(() -> new IllegalArgumentException("해당 재고가 존재하지 않습니다."));

    // 재고 수량 확인
    if (inventory.getTotalQty() < outgoingQty) {
      throw new IllegalStateException("재고 수량이 부족하여 출고를 진행할 수 없습니다.");
    }

    String outgoingCode = generateOutgoingCode();

    Outgoing outgoing = Outgoing.builder()
            .outgoingTotal(outgoingTotal)
            .inventory(inventory)
            .outgoingQty(outgoingQty)
            .outgoingCode(outgoingCode)
            .outgoingDate(LocalDateTime.now())
            .build();
    outgoingRepository.save(outgoing);

    outgoingTotal.addToTotalQty(dto.getOutgoingQty());

    outgoingTotal.markFirstOutgoing();

    outgoingTotalRepository.save(outgoingTotal);

    outgoingTotalService.updateOutgoingStatus(outgoing.getOutgoingTotal().getOutgoingTotalId());

  }

  // 출고
  @Override
  public void fullOutgoing(List<OutgoingDTO> dtoList) {
    for (OutgoingDTO dto : dtoList) {

      OutgoingTotal outgoingTotal = outgoingTotalRepository.findById(dto.getOutgoingTotalId())
              .orElseThrow(() -> new IllegalArgumentException("해당 출고 토탈 항목이 없습니다."));

      int outgoingQty = dto.getOutgoingQty();

      validateOutgoingQty(outgoingQty);

      int expectedQty = outgoingTotal.getEstimatedOutgoingQty();
      int remainingQty = expectedQty - outgoingTotal.getOutgoingTotalQty();

      if (remainingQty <= 0) {
        throw new IllegalStateException("남은 출고 수량이 없습니다.");
      }
      if (outgoingQty > remainingQty) {
        throw new IllegalStateException("출고 수량이 남은 수량을 초과합니다.");
      }

      Inventory inventory = inventoryRepository.findById(dto.getInventoryId())
              .orElseThrow(() -> new IllegalArgumentException("해당 재고가 존재하지 않습니다."));

      // 재고 수량 확인
      if (inventory.getTotalQty() < outgoingQty) {
        throw new IllegalStateException("재고 수량이 부족하여 출고를 진행할 수 없습니다.");
      }

      String outgoingCode = generateOutgoingCode();

      Outgoing outgoing = Outgoing.builder()
              .outgoingTotal(outgoingTotal)
              .inventory(inventory)
              .outgoingQty(outgoingQty)
              .outgoingCode(outgoingCode)
              .outgoingDate(LocalDateTime.now())
              .build();
      outgoingRepository.save(outgoing);

      outgoingTotal.addToTotalQty(outgoingQty);

      outgoingTotal.markFirstOutgoing();

      outgoingTotalRepository.save(outgoingTotal);

      outgoingTotalService.updateOutgoingStatus(outgoingTotal.getOutgoingTotalId());

    }
  }

  private void validateOutgoingQty(int qty) {
    if (qty < 0) {
      throw new IllegalArgumentException("출고 수량은 음수일 수 없습니다.");
    }
  }

  @Override
  public PageResponseDTO<OutgoingDTO> listWithOutgoing(PageRequestDTO pageRequestDTO) {

    // 검색 조건을 받아옵니다.
    LocalDate outgoingCompletedAtStart = pageRequestDTO.getOutgoingCompletedAtStart();
    LocalDate outgoingCompletedAtEnd = pageRequestDTO.getOutgoingCompletedAtEnd();
    String outgoingCode = pageRequestDTO.getOutgoingCode();
    String matId = pageRequestDTO.getMatId();
    String matName = pageRequestDTO.getMatName();

    // Pageable을 pageRequestDTO에서 받아옵니다.
    Pageable pageable = pageRequestDTO.getPageable("outgoingId");

    // 검색 조건과 페이지 정보를 이용하여 데이터를 조회합니다.
    Page<Outgoing> result = outgoingRepository
            .searchOutgoing(outgoingCompletedAtStart, outgoingCompletedAtEnd,
                    outgoingCode, matId, matName, pageable);

    // 조회된 데이터(DeliveryRequest)를 DeliveryRequestDTO로 변환합니다.
    List<OutgoingDTO> dtoList = new ArrayList<>();

    for (Outgoing outgoing : result.getContent()) {

      // DTO 생성
      OutgoingDTO dto = OutgoingDTO.builder()
              .outgoingId(outgoing.getOutgoingId())
              .outgoingDate(outgoing.getOutgoingDate())
              .outgoingCode(outgoing.getOutgoingCode())
              .matId(outgoing.getOutgoingTotal().getMaterial().getMatId())
              .matName(outgoing.getOutgoingTotal().getMaterial().getMatName())
              .outgoingQty(outgoing.getOutgoingQty())
              .build();

      dtoList.add(dto);
    }

    // PageResponseDTO로 변환하여 반환합니다.
    return PageResponseDTO.<OutgoingDTO>withAll()
            .pageRequestDTO(pageRequestDTO)
            .dtoList(dtoList)
            .total((int) result.getTotalElements())
            .build();
  }
}
