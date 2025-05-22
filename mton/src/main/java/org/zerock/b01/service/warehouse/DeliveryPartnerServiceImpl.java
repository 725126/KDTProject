package org.zerock.b01.service.warehouse;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.zerock.b01.domain.operation.Ordering;
import org.zerock.b01.domain.warehouse.*;
import org.zerock.b01.dto.PageRequestDTO;
import org.zerock.b01.dto.PageResponseDTO;
import org.zerock.b01.dto.warehouse.DeliveryPartnerDTO;
import org.zerock.b01.repository.warehouse.DeliveryPartnerRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional
public class DeliveryPartnerServiceImpl implements DeliveryPartnerService{

  private final DeliveryPartnerRepository deliveryPartnerRepository;
  private final DeliveryPartnerItemService deliveryPartnerItemService;
  private final IncomingService incomingService;

  @Override
  public PageResponseDTO<DeliveryPartnerDTO> listWithDeliveryPartner(PageRequestDTO pageRequestDTO, Long partnerId) {

    // 검색 조건을 받아옵니다.
    String drItemCode = pageRequestDTO.getDrItemCode();
    String orderId = pageRequestDTO.getOrderId();
    String matName = pageRequestDTO.getMatName();
    LocalDate drItemDueDateStart = pageRequestDTO.getDrItemDueDateStart();
    LocalDate drItemDueDateEnd = pageRequestDTO.getDrItemDueDateEnd();

    // Pageable을 pageRequestDTO에서 받아옵니다.
    Pageable pageable = pageRequestDTO.getPageable("deliveryPartnerId");

    // 검색 조건과 페이지 정보를 이용하여 데이터를 조회합니다.
    Page<DeliveryPartner> result = deliveryPartnerRepository
            .searchDeliveryPartnerAll(drItemCode, orderId, matName,
                    drItemDueDateStart, drItemDueDateEnd, partnerId,pageable);


    List<DeliveryPartnerDTO> dtoList = new ArrayList<>();



    for (DeliveryPartner deliveryPartner : result.getContent()) {

      DeliveryRequestItem deliveryRequestItem = deliveryPartner.getDeliveryRequestItem();
      Ordering ordering = deliveryPartner.getDeliveryRequestItem().getDeliveryRequest().getOrdering();
      IncomingTotal incomingTotal = deliveryPartner.getIncomingTotal();

      // DTO 생성
      DeliveryPartnerDTO dto = DeliveryPartnerDTO.builder()
              .deliveryPartnerId(deliveryPartner.getDeliveryPartnerId())
              .deliveryPartnerQty(deliveryPartner.getDeliveryPartnerQty())
              .deliveryPartnerStatus(deliveryPartner.getDeliveryPartnerStatus().name())
              .drItemCode(deliveryRequestItem.getDrItemCode())
              .orderId(ordering.getOrderId())
              .matName(ordering.getContractMaterial().getMaterial().getMatName())
              .drItemQty(deliveryRequestItem.getDrItemQty())
              .drItemDueDate(deliveryRequestItem.getDrItemDueDate())
              .incomingReturnTotalQty(incomingTotal != null ? incomingTotal.getIncomingReturnTotalQty() : 0)
              .incomingMissingTotalQty(incomingTotal != null ? incomingTotal.getIncomingMissingTotalQty() : 0)
              .partnerId(ordering.getContractMaterial().getContract().getPartner().getPartnerId())
              .build();

      dtoList.add(dto);
    }
    // PageResponseDTO로 변환하여 반환합니다.
    return PageResponseDTO.<DeliveryPartnerDTO>withAll()
            .pageRequestDTO(pageRequestDTO)
            .dtoList(dtoList)
            .total((int) result.getTotalElements())
            .build();
  }

  // 부분출하
  @Override
  public void partialDelivery(DeliveryPartnerDTO dto) {
    validateDeliveryQty(dto.getDeliveryPartnerQty());

    DeliveryPartner deliveryPartner = deliveryPartnerRepository.findById(dto.getDeliveryPartnerId())
            .orElseThrow(() -> new IllegalArgumentException("해당 항목 없음"));

    // 출하 수량 누적
    deliveryPartner.updateDeliveryPartnerQty(dto.getDeliveryPartnerQty());

    deliveryPartnerRepository.save(deliveryPartner);

    // 이력 저장 후 반환
    DeliveryPartnerItem savedItem =
            deliveryPartnerItemService.saveDeliveryPartnerItem(deliveryPartner, dto.getDeliveryPartnerQty());

    // 입고 생성
    incomingService.createIncomingForDeliveryPartnerItem(savedItem);

    // 출하 상태 갱신
    updateDeliveryPartnerStatus(deliveryPartner);

  }

  // 출하
  @Override
  public void fullDelivery(List<DeliveryPartnerDTO> dtoList) {
    for (DeliveryPartnerDTO dto : dtoList) {

      DeliveryPartner deliveryPartner = deliveryPartnerRepository.findById(dto.getDeliveryPartnerId())
              .orElseThrow(() -> new IllegalArgumentException("해당 항목 없음"));

      int totalQty = dto.getDeliveryPartnerQty(); // 전체출하량
      deliveryPartner.updateDeliveryPartnerQty(totalQty);

      deliveryPartnerRepository.save(deliveryPartner);

      // 이력 저장 + 반환
      DeliveryPartnerItem savedItem = deliveryPartnerItemService.saveDeliveryPartnerItem(deliveryPartner, totalQty);

      // 입고 엔티티 생성
      incomingService.createIncomingForDeliveryPartnerItem(savedItem);

      // 출하 상태 갱신
      updateDeliveryPartnerStatus(deliveryPartner);

    }
  }

  private void validateDeliveryQty(int qty) {
    if (qty < 0) {
      throw new IllegalArgumentException("출하 수량은 음수일 수 없습니다.");
    }
  }

  @Override
  public void updateDeliveryPartnerStatus(DeliveryPartner deliveryPartner) {
    int drQty = deliveryPartner.getDeliveryRequestItem().getDrItemQty();
    int shippedQty = deliveryPartner.getDeliveryPartnerQty();
    int returnedQty = deliveryPartner.getIncomingTotal().getIncomingReturnTotalQty();
    int missingQty = deliveryPartner.getIncomingTotal().getIncomingMissingTotalQty();
    int netQty = shippedQty - returnedQty - missingQty;

    // 출하가 전혀 안 된 경우
    if (shippedQty == 0) {
      deliveryPartner.updateDeliveryPartnerStatus(DeliveryPartnerStatus.진행중);

      // 출하했지만 반품 등으로 실질 수량이 줄어든 경우
    } else if (netQty < drQty) {
      deliveryPartner.updateDeliveryPartnerStatus(DeliveryPartnerStatus.부분출하);

      // 출하 완료 수량이고, 입고도 마감됨
    } else if (netQty == drQty) {
      if (deliveryPartner.getIncomingTotal().getIncomingStatus() == IncomingStatus.입고마감) {
        deliveryPartner.updateDeliveryPartnerStatus(DeliveryPartnerStatus.완료);
      } else {
        deliveryPartner.updateDeliveryPartnerStatus(DeliveryPartnerStatus.출하);
      }

    } else {
      throw new IllegalStateException("반품 수량이 출하 수량보다 많습니다. 데이터 확인이 필요합니다.");
    }

  }
}
