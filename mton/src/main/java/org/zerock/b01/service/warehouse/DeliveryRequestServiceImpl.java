package org.zerock.b01.service.warehouse;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.zerock.b01.domain.operation.Ordering;
import org.zerock.b01.domain.warehouse.CompanyStorage;
import org.zerock.b01.domain.warehouse.DeliveryRequest;
import org.zerock.b01.domain.warehouse.DeliveryStatus;
import org.zerock.b01.dto.PageRequestDTO;
import org.zerock.b01.dto.PageResponseDTO;
import org.zerock.b01.dto.warehouse.DeliveryRequestDTO;
import org.zerock.b01.repository.warehouse.CompanyStorageRepository;
import org.zerock.b01.repository.warehouse.DeliveryRequestItemRepository;
import org.zerock.b01.repository.warehouse.DeliveryRequestRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class DeliveryRequestServiceImpl implements DeliveryRequestService {

  private final DeliveryRequestRepository deliveryRequestRepository;
  private final OrderingRepository orderingRepository;
  private final CompanyStorageRepository companyStorageRepository;
  private final DeliveryRequestItemRepository deliveryRequestItemRepository;

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
            .dtoList(dtoList)
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
  public List<DeliveryRequestDTO> readDeliveryRequestAll() {

    List<DeliveryRequest> result = deliveryRequestRepository.findAll();

    List<DeliveryRequestDTO> deliveryRequestDTOList = result.stream()
                    .map(this::entityToDto)
                    .collect(Collectors.toList());

    return deliveryRequestDTOList;
  }

  public void updateDeliveryRequestStatus(Long drId) {
    DeliveryRequest dr = deliveryRequestRepository.findById(drId)
            .orElseThrow(() -> new RuntimeException("해당 납입지시 없음"));

    int drTotalQty = deliveryRequestItemRepository.getTotalQtyByDrId(drId);

    dr.updateTotalQty(drTotalQty);

    // 엔티티 내부의 메서드를 사용하여 상태 변경
    dr.updateStatus();

    deliveryRequestRepository.save(dr);
  }

}
