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
import org.zerock.b01.dto.operation.OrderingDTO;
import org.zerock.b01.dto.warehouse.DeliveryRequestDTO;
import org.zerock.b01.repository.warehouse.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class DeliveryRequestServiceImpl implements DeliveryRequestService {

  private final DeliveryRequestRepository deliveryRequestRepository;
  private final CompanyStorageRepository companyStorageRepository;
  private final DeliveryRequestItemRepository deliveryRequestItemRepository;
  private final OrderingRepository orderingRepository;
  private final DeliveryPartnerRepository deliveryPartnerRepository;
  private final IncomingItemRepository incomingItemRepository;

  @Override
  public PageResponseDTO<DeliveryRequestDTO> listWithDeliveryRequest(PageRequestDTO pageRequestDTO) {

    // 검색 조건을 받아옵니다.
    String orderId = pageRequestDTO.getOrderId();
    String matName = pageRequestDTO.getMatName();
    LocalDate orderDateStart = pageRequestDTO.getOrderDateStart();
    LocalDate orderDateEnd = pageRequestDTO.getOrderDateEnd();
    LocalDate orderEndStart = pageRequestDTO.getOrderEndStart();
    LocalDate orderEndEnd = pageRequestDTO.getOrderEndEnd();

    // Pageable을 pageRequestDTO에서 받아옵니다.
    Pageable pageable = pageRequestDTO.getPageable("drId");

    // 검색 조건과 페이지 정보를 이용하여 데이터를 조회합니다.
    Page<DeliveryRequest> result = deliveryRequestRepository
            .searchDeliveryRequestAll(orderId, matName, orderDateStart, orderDateEnd,
                                                  orderEndStart, orderEndEnd, pageable);

    // 조회된 데이터(DeliveryRequest)를 DeliveryRequestDTO로 변환합니다.
    List<DeliveryRequestDTO> dtoList = new ArrayList<>();

    for (DeliveryRequest dr : result.getContent()) {
      Ordering ordering = dr.getOrdering();
      int totalQty = deliveryRequestItemRepository.getTotalQtyByDrId(dr.getDrId());

      // 상태 계산 (납품수량이 발주량에 도달했는지 확인)
      DeliveryStatus status = (totalQty >= ordering.getOrderQty()) ? DeliveryStatus.완료 : DeliveryStatus.진행중;

      // DTO 생성
      DeliveryRequestDTO dto = DeliveryRequestDTO.builder()
              .drId(dr.getDrId())
              .orderId(ordering.getOrderId())
              .matName(ordering.getContractMaterial().getMaterial().getMatName())
              .orderDate(ordering.getOrderDate())
              .orderEnd(ordering.getOrderEnd())
              .orderQty(ordering.getOrderQty())
              .drTotalQty(totalQty)
              .drStatus(status.toString())
              .build();

      dtoList.add(dto);
    }

    // PageResponseDTO로 변환하여 반환합니다.
    return PageResponseDTO.<DeliveryRequestDTO>withAll()
            .pageRequestDTO(pageRequestDTO)
            .dtoList(
                    dtoList.stream()
                            .sorted((a, b) -> {
                              if (a.getDrStatus().equals("완료") && !b.getDrStatus().equals("완료")) {
                                return 1;
                              } else if (!a.getDrStatus().equals("완료") && b.getDrStatus().equals("완료")) {
                                return -1;
                              } else {
                                return 0;
                              }
                            })
                            .collect(Collectors.toList())
            )
            .total((int) result.getTotalElements())
            .build();
  }


  @Override
  public List<CompanyStorage> getCompanyStorageList() {
    return companyStorageRepository.findAll(); // 또는 조건에 따라 필터링
  }

  @Override
  public DeliveryRequestDTO readDeliveryRequestOne(String orderId) {

    Optional<DeliveryRequest> result = deliveryRequestRepository.findByOrdering_OrderId(orderId);

    DeliveryRequest deliveryRequest = result.orElseThrow();

    DeliveryRequestDTO deliveryRequestDTO = entityToDto(deliveryRequest);

    return deliveryRequestDTO;
  }

  @Override
  public void updateDeliveryRequestStatus(Long drId) {
    DeliveryRequest dr = deliveryRequestRepository.findById(drId)
            .orElseThrow(() -> new RuntimeException("해당 납입지시 없음"));

    int drTotalQty = deliveryRequestItemRepository.getTotalQtyByDrId(drId);

    dr.updateTotalQty(drTotalQty);

    // 엔티티 내부의 메서드를 사용하여 상태 변경
    dr.updateStatus();

    deliveryRequestRepository.save(dr);
  }

  @Override
  public void createDeliveryRequestFromOrdering(String orderId) {
    Ordering ordering = orderingRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("발주정보가 존재하지 않습니다."));

    //납입지시 생성
    DeliveryRequest dr = DeliveryRequest.builder()
            .drStatus(DeliveryStatus.진행중)
            .drTotalQty(0)
            .ordering(ordering)
            .build();

    deliveryRequestRepository.save(dr);
  }

  @Override
  public void deleteByOrderIds(List<String> orderIds) {
    List<DeliveryRequest> requests = deliveryRequestRepository.findByOrderingOrderIdIn(orderIds);

    for (DeliveryRequest request : requests) {
      List<DeliveryRequestItem> items = deliveryRequestItemRepository.findByDeliveryRequest(request);

      if (items.isEmpty()) {
        // DeliveryRequestItem이 없으면 바로 DeliveryRequest 삭제
        deliveryRequestRepository.delete(request);
        continue;
      }

      for (DeliveryRequestItem item : items) {
        List<DeliveryPartner> partners = deliveryPartnerRepository.findAllByDeliveryRequestItem(item);

        for (DeliveryPartner partner : partners) {
          if (partner.getDeliveryPartnerStatus() != DeliveryPartnerStatus.진행중) {
            throw new IllegalStateException(
                    "발주 ID: " + request.getOrdering().getOrderId() + "는 출하중이므로 삭제할 수 없습니다."
            );
          }
        }

        deliveryPartnerRepository.deleteAll(partners);
        deliveryRequestItemRepository.delete(item);
      }

      deliveryRequestRepository.delete(request);
    }
  }

  @Override
  public void validateOrderingUpdate(List<OrderingDTO> list) {
    List<String> orderIds = list.stream()
            .map(OrderingDTO::getOrderId)
            .collect(Collectors.toList());

    Map<String, Ordering> existingOrders = orderingRepository.findAllById(orderIds).stream()
            .collect(Collectors.toMap(Ordering::getOrderId, o -> o));

    for (OrderingDTO dto : list) {
      Ordering existing = existingOrders.get(dto.getOrderId());

      List<DeliveryRequest> requests = deliveryRequestRepository.findByOrdering(existing);

      for (DeliveryRequest request : requests) {
        // 여기서 수량 제한 체크를 DeliveryRequest의 drTotalQty로 변경
        int drTotalQty = request.getDrTotalQty();

        if (dto.getOrderQty() < drTotalQty) {
          throw new IllegalStateException(
                  "발주 ID: " + existing.getOrderId()
                          + "는 이미 납입 요청된 수량(" + drTotalQty + ")보다 작게 수정할 수 없습니다."
          );
        }

        List<DeliveryRequestItem> items = deliveryRequestItemRepository.findByDeliveryRequest(request);

        for (DeliveryRequestItem item : items) {
          List<DeliveryPartner> partners = deliveryPartnerRepository.findAllByDeliveryRequestItem(item);

          for (DeliveryPartner partner : partners) {
            // 입고 이력 중 가장 마지막 수정일(LocalDateTime)을 LocalDate로 변환하여 납기일과 비교
            List<IncomingItem> incomingItems = incomingItemRepository.findByIncoming_IncomingTotal(partner.getIncomingTotal());
            Optional<LocalDate> latestModifyDate = incomingItems.stream()
                    .map(IncomingItem::getModifyDate)
                    .map(LocalDateTime::toLocalDate)
                    .max(LocalDate::compareTo);

            if (latestModifyDate.isPresent()) {
              LocalDate latest = latestModifyDate.get();
              if (dto.getOrderEnd().isBefore(latest)) {
                throw new IllegalStateException(
                        "발주 ID: " + existing.getOrderId()
                                + "의 납기일(" + dto.getOrderEnd()
                                + ")은 마지막 입고일(" + latest.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                                + ")보다 빠를 수 없습니다."
                );
              }
            }
          }
        }
      }
    }
  }

}
