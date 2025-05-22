package org.zerock.b01.service.warehouse;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.zerock.b01.controller.operation.repository.OrderingRepository;
import org.zerock.b01.domain.operation.Ordering;
import org.zerock.b01.domain.warehouse.*;
import org.zerock.b01.dto.PageRequestDTO;
import org.zerock.b01.dto.PageResponseDTO;
import org.zerock.b01.dto.warehouse.IncomingInspectionDTO;
import org.zerock.b01.dto.warehouse.IncomingTotalDTO;
import org.zerock.b01.repository.warehouse.DeliveryPartnerRepository;
import org.zerock.b01.repository.warehouse.DeliveryRequestItemRepository;
import org.zerock.b01.repository.warehouse.IncomingTotalRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Log4j2
public class IncomingTotalServiceImpl implements IncomingTotalService {

  private final IncomingTotalRepository incomingTotalRepository;
  private final DeliveryRequestItemRepository deliveryRequestItemRepository;
  private final OrderingRepository orderingRepository;
  private final TransactionItemService transactionItemService;

  public void updateIncomingStatus(Long drItemId) {
    DeliveryRequestItem item = deliveryRequestItemRepository.findById(drItemId)
            .orElseThrow(() -> new IllegalArgumentException("해당 납입지시 항목이 존재하지 않습니다."));

    IncomingTotal incomingTotal = incomingTotalRepository.findByDeliveryRequestItem(item)
            .orElseThrow(() -> new IllegalStateException("해당 납입지시 항목에 대한 입고정보가 없습니다."));

    int effectiveQty = incomingTotal.getIncomingEffectiveQty();  // 유효 입고 수량
    int expectedQty = item.getDrItemQty(); // 납입지시 수량

    IncomingStatus newStatus;
    if (effectiveQty == 0) {
      newStatus = IncomingStatus.미입고;
    } else if (effectiveQty < expectedQty) {
      newStatus = IncomingStatus.부분입고;
    } else {
      newStatus = IncomingStatus.입고마감대기중;
    }

    incomingTotal.updateIncomingStatus(newStatus);  // 상태만 수정
    incomingTotalRepository.save(incomingTotal);   // 저장
  }


  public void closeIncoming(Long incomingTotalId) {
    IncomingTotal incomingTotal = incomingTotalRepository.findById(incomingTotalId)
            .orElseThrow(() -> new IllegalArgumentException("해당 납입지시 항목이 존재하지 않습니다."));

    int totalQty = incomingTotal.getIncomingEffectiveQty();
    int expectedQty = incomingTotal.getDeliveryRequestItem().getDrItemQty();

    if (totalQty != expectedQty) {
      throw new IllegalStateException("전량 입고된 경우에만 마감할 수 있습니다.");
    }

    incomingTotal.markAsCompleted();
    incomingTotalRepository.save(incomingTotal);

    String orderId = incomingTotal.getDeliveryRequestItem()
            .getDeliveryRequest()
            .getOrdering()
            .getOrderId();

    updateOrderIngStatusIfAllIncomingClosed(orderId);

  }

  public void updateOrderIngStatusIfAllIncomingClosed(String orderId) {
    boolean hasOpenIncoming = incomingTotalRepository.existsByOrderIdAndStatusNot(orderId, IncomingStatus.입고마감);

    if (!hasOpenIncoming) {
      Ordering ordering = orderingRepository.findById(orderId)
              .orElseThrow(() -> new IllegalArgumentException("해당 발주 항목이 존재하지 않습니다."));

      ordering.markAsCompleted();
      orderingRepository.save(ordering);

    }
  }

  @Override
  public IncomingTotalDTO readIncomingTotalOne(Long drItemId) {

    Optional<IncomingTotal> result = incomingTotalRepository.findByDeliveryRequestItem_drItemId(drItemId);

    IncomingTotal incomingTotal = result.orElseThrow();

    IncomingTotalDTO incomingTotalDTO = entityToIncomingTotalDto(incomingTotal);

    return incomingTotalDTO;
  }

  @Override
  public PageResponseDTO<IncomingTotalDTO> listWithIncomingTotal(PageRequestDTO pageRequestDTO) {

    // 검색 조건을 받아옵니다.
    LocalDate incomingCompletedAtStart = pageRequestDTO.getIncomingCompletedAtStart();
    LocalDate incomingCompletedAtEnd = pageRequestDTO.getIncomingCompletedAtEnd();
    String pCompany = pageRequestDTO.getPCompany();
    String matId = pageRequestDTO.getMatId();
    String matName = pageRequestDTO.getMatName();

    // Pageable을 pageRequestDTO에서 받아옵니다.
    Pageable pageable = pageRequestDTO.getPageable("incomingTotalId");

    // 검색 조건과 페이지 정보를 이용하여 데이터를 조회합니다.
    Page<IncomingTotal> result = incomingTotalRepository
            .searchIncomingTotal(incomingCompletedAtStart, incomingCompletedAtEnd,
                    pCompany, matId, matName, pageable);

    // 조회된 데이터(DeliveryRequest)를 DeliveryRequestDTO로 변환합니다.
    List<IncomingTotalDTO> dtoList = new ArrayList<>();



    for (IncomingTotal incomingTotal : result.getContent()) {

      DeliveryRequestItem deliveryRequestItem = incomingTotal.getDeliveryRequestItem();

      // DTO 생성
      IncomingTotalDTO dto = IncomingTotalDTO.builder()
              .incomingTotalId(incomingTotal.getIncomingTotalId())
              .incomingCompletedAt(incomingTotal.getIncomingCompletedAt())
              .pCompany(deliveryRequestItem.getDeliveryRequest().getOrdering()
                      .getContractMaterial().getContract().getPartner().getPCompany())
              .matId(deliveryRequestItem.getDeliveryRequest().getOrdering()
                      .getContractMaterial().getMaterial().getMatId())
              .matName(deliveryRequestItem.getDeliveryRequest().getOrdering()
                      .getContractMaterial().getMaterial().getMatName())
              .incomingEffectiveQty(incomingTotal.getIncomingEffectiveQty())
              .build();

      dtoList.add(dto);
    }

    // PageResponseDTO로 변환하여 반환합니다.
    return PageResponseDTO.<IncomingTotalDTO>withAll()
            .pageRequestDTO(pageRequestDTO)
            .dtoList(dtoList)
            .total((int) result.getTotalElements())
            .build();
  }

}
