package org.zerock.b01.service.warehouse;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.zerock.b01.domain.operation.Ordering;
import org.zerock.b01.domain.warehouse.*;
import org.zerock.b01.dto.PageRequestDTO;
import org.zerock.b01.dto.PageResponseDTO;
import org.zerock.b01.dto.warehouse.DeliveryRequestItemDTO;
import org.zerock.b01.repository.search.warehouse.IncomingSearch;
import org.zerock.b01.repository.warehouse.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional
public class DeliveryRequestItemServiceImpl implements DeliveryRequestItemService {

  private final DeliveryRequestItemRepository deliveryRequestItemRepository;
  private final DeliveryRequestRepository deliveryRequestRepository;
  private final DeliveryRequestService deliveryRequestService;
  private final CompanyStorageRepository companyStorageRepository;
  private final DeliveryPartnerRepository deliveryPartnerRepository;
  private final IncomingTotalRepository incomingTotalRepository;


  @Override
  public String generateDrItemCode() {
    String todayPrefix = "DR-" + java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd")) + "-";
    List<String> result = deliveryRequestItemRepository.findTopByPrefix(todayPrefix, PageRequest.of(0, 1));
    String lastCode = result.isEmpty() ? null : result.get(0);


    int nextNumber = 1;
    if (lastCode != null) {
      String numberPart = lastCode.substring(todayPrefix.length());
      nextNumber = Integer.parseInt(numberPart) + 1;
    }

    return todayPrefix + String.format("%04d", nextNumber);
  }


  @Override
  public List<DeliveryRequestItemDTO> registerDeliveryRequestItem(List<DeliveryRequestItemDTO> dtoList) {

    List<DeliveryRequestItemDTO> resultList = new ArrayList<>();

    for (DeliveryRequestItemDTO dto : dtoList) {
      if (dto.getDrItemQty() < 0) {
        throw new IllegalArgumentException("납입 지시 수량은 음수일 수 없습니다.");
      }

      // 필요한 객체를 준비
      DeliveryRequest deliveryRequest = deliveryRequestRepository.findById(dto.getDrId())
              .orElseThrow(() -> new IllegalArgumentException("해당 납입지시(drId)를 찾을 수 없습니다: " + dto.getDrId()));

      // 발주 수량 및 납입 지시 수량 체크
      int orderQty = deliveryRequest.getOrdering().getOrderQty();
      int drTotalQty = deliveryRequest.getDrTotalQty();
      int newTotal = drTotalQty + dto.getDrItemQty();

      if (newTotal > orderQty) {
        throw new IllegalArgumentException("납입 지시 수량이 발주량을 초과할 수 없습니다.");
      }

      // 창고 정보
      CompanyStorage companyStorage = companyStorageRepository.findById(dto.getCstorageId())
              .orElseThrow(() -> new IllegalArgumentException("해당 창고를 찾을 수 없습니다: " + dto.getCstorageId()));

      // 코드 생성
      String drItemCode = generateDrItemCode();

      // 납입 지시 아이템 객체 생성
      DeliveryRequestItem deliveryRequestItem = DeliveryRequestItem.builder()
              .deliveryRequest(deliveryRequest)
              .companyStorage(companyStorage)
              .drItemCode(drItemCode)  // 생성된 코드 포함
              .drItemQty(dto.getDrItemQty())
              .drItemDueDate(dto.getDrItemDueDate())
              .build();

      // 기타 엔티티 준비 (IncomingTotal, DeliveryPartner)
      IncomingTotal incomingTotal = IncomingTotal.builder()
              .deliveryRequestItem(deliveryRequestItem)
              .incomingEffectiveQty(0)
              .incomingTotalQty(0)
              .incomingReturnTotalQty(0)
              .incomingMissingTotalQty(0)
              .incomingStatus(IncomingStatus.미입고)
              .build();

      DeliveryPartner deliveryPartner = DeliveryPartner.builder()
              .deliveryRequestItem(deliveryRequestItem)
              .incomingTotal(incomingTotal)
              .deliveryPartnerQty(0)
              .deliveryPartnerStatus(DeliveryPartnerStatus.진행중)
              .build();

      // 저장
      deliveryRequestItemRepository.save(deliveryRequestItem);
      incomingTotalRepository.save(incomingTotal);
      deliveryPartnerRepository.save(deliveryPartner);

      // DTO에 생성된 코드 포함하여 반환
      DeliveryRequestItemDTO resultDTO = DeliveryRequestItemDTO.builder()
              .drItemCode(deliveryRequestItem.getDrItemCode())  // 생성된 코드 포함
              .drId(deliveryRequestItem.getDeliveryRequest().getDrId())
              .drItemQty(deliveryRequestItem.getDrItemQty())
              .drItemDueDate(deliveryRequestItem.getDrItemDueDate())
              .cstorageId(deliveryRequestItem.getCompanyStorage().getCstorageId())
              .build();

      resultList.add(resultDTO);
    }

    // ✅ 수량 및 상태 갱신
    if (!dtoList.isEmpty()) {
      Long drId = dtoList.get(0).getDrId();
      deliveryRequestService.updateDeliveryRequestStatus(drId);
    }

    return resultList;
  }

  @Override
  public DeliveryRequestItemDTO readDeliveryRequestItem(Long drItemId) {

    Optional<DeliveryRequestItem> ItemOptional = deliveryRequestItemRepository.findById(drItemId);

    DeliveryRequestItem deliveryRequestItem = ItemOptional.orElseThrow();

    DeliveryRequestItemDTO dto = DeliveryRequestItemDTO.builder()
            .drItemId(deliveryRequestItem.getDrItemId())
            .drId(deliveryRequestItem.getDeliveryRequest().getDrId())
            .drItemCode(deliveryRequestItem.getDrItemCode())
            .drItemQty(deliveryRequestItem.getDrItemQty())
            .drItemDueDate(deliveryRequestItem.getDrItemDueDate())
            .cstorageId(deliveryRequestItem.getCompanyStorage().getCstorageId())
            .build();

    return dto;
  }

  @Override
  public void modifyDeliveryRequestItem(DeliveryRequestItemDTO deliveryRequestItemDTO) {

    Optional<DeliveryRequestItem> ItemOptional =
            deliveryRequestItemRepository.findById(deliveryRequestItemDTO.getDrItemId());

    DeliveryRequestItem deliveryRequestItem = ItemOptional.orElseThrow();

    // 출고 수량 유효성 검사 추가
    Optional<DeliveryPartner> dpOptional = deliveryPartnerRepository.findByDeliveryRequestItem(deliveryRequestItem);
    if (dpOptional.isPresent()) {
      DeliveryPartner deliveryPartner = dpOptional.get();
      int alreadyDeliveredQty = deliveryPartner.getDeliveryPartnerQty();
      int returnQty = (deliveryPartner.getIncomingTotal() != null)
              ? deliveryPartner.getIncomingTotal().getIncomingReturnTotalQty() : 0;
      int newQty = deliveryRequestItemDTO.getDrItemQty();

      if (newQty < 0) {
        throw new IllegalArgumentException("수정하려는 수량은 음수일 수 없습니다.");
      }

      int netDeliveredQty = alreadyDeliveredQty - returnQty;

      if (newQty < netDeliveredQty) {
        throw new IllegalStateException("수정하려는 수량은(" + netDeliveredQty + ")보다 작을 수 없습니다.");
      }
    }

    String cstorageId = deliveryRequestItemDTO.getCstorageId();

    CompanyStorage companyStorage = companyStorageRepository.findById(cstorageId)
            .orElseThrow(() -> new IllegalArgumentException("해당 창고가 존재하지 않습니다: " + cstorageId));

    deliveryRequestItem.updateDeliveryRequestItem(
            deliveryRequestItemDTO.getDrItemQty(),
            deliveryRequestItemDTO.getDrItemDueDate(),
            companyStorage
    );

    deliveryRequestItemRepository.save(deliveryRequestItem);

    // DeliveryPartner에 연결된 deliveryRequestItem도 갱신
    DeliveryPartner deliveryPartner = deliveryPartnerRepository.findByDeliveryRequestItem(deliveryRequestItem)
            .orElseThrow(() -> new IllegalStateException("해당 납입지시 항목에 대한 협력사 정보가 없습니다."));

    deliveryPartner.updateDeliveryRequestItem(deliveryRequestItem);
    deliveryPartnerRepository.save(deliveryPartner);

    // 해당 DeliveryRequestItem과 연관된 DeliveryRequest를 가져옵니다.
    DeliveryRequest deliveryRequest = deliveryRequestItem.getDeliveryRequest();

    // 상태 업데이트 (해당 DeliveryRequest의 상태를 최신화)
    deliveryRequestService.updateDeliveryRequestStatus(deliveryRequest.getDrId());
  }

  @Override
  public void removeDeliveryRequestItem(Long drItemId) {

    // 해당 납입 지시 항목을 조회
    Optional<DeliveryRequestItem> itemOptional = deliveryRequestItemRepository.findById(drItemId);
    if (itemOptional.isPresent()) {
      // 삭제할 납입 지시 항목을 가져옴
      DeliveryRequestItem deliveryRequestItem = itemOptional.get();

      // 해당 납입 지시 항목에 연관된 DeliveryPartner를 찾습니다.
      Optional<DeliveryPartner> deliveryPartnerOptional =
              deliveryPartnerRepository.findByDeliveryRequestItem(deliveryRequestItem);

      // 연관된 DeliveryPartner가 있으면 삭제
      if (deliveryPartnerOptional.isPresent()) {
        DeliveryPartner deliveryPartner = deliveryPartnerOptional.get();
        IncomingTotal incomingTotal = deliveryPartner.getIncomingTotal();

        int drItemQty = deliveryRequestItem.getDrItemQty();
        int deliveryPartnerQty = deliveryPartner.getDeliveryPartnerQty();
        int incomingReturnTotalQty = incomingTotal != null ? incomingTotal.getIncomingReturnTotalQty() : 0;

        int remainingQty = drItemQty - deliveryPartnerQty + incomingReturnTotalQty;

        // 남은 수량이 전체 수량과 동일해야 삭제 허용
        if (remainingQty != drItemQty) {
          throw new IllegalStateException("출고 후 전량 반품된 경우에만 삭제할 수 있습니다.");
        }

        deliveryPartnerRepository.delete(deliveryPartner);
      }

      // 삭제 후 상태 업데이트 (삭제된 납입 지시 항목의 drId를 사용)
      Long drId = deliveryRequestItem.getDeliveryRequest().getDrId();

      // 납입 지시 항목 삭제
      deliveryRequestItemRepository.deleteById(drItemId);

      // 삭제 후 상태 업데이트
      deliveryRequestService.updateDeliveryRequestStatus(drId);
    } else {
      throw new IllegalArgumentException("해당 납입지시 항목이 존재하지 않습니다: " + drItemId);
    }
  }

  @Override
  public LocalDate getLastDrItemDueDate(Long drId) {
    return deliveryRequestItemRepository.findLastDrItemDueDateByDrId(drId);

  }

  @Override
  public PageResponseDTO<DeliveryRequestItemDTO> getListDeliveryRequestItem(Long drId,
                                                                            PageRequestDTO pageRequestDTO) {

    Pageable pageable = PageRequest.of(pageRequestDTO.getPage() <= 0? 0: pageRequestDTO.getPage() -1
            , pageRequestDTO.getSize(),
            Sort.by("drItemId").ascending());

    Page<DeliveryRequestItem> result = deliveryRequestItemRepository.listDeliveryRequestItem(drId,pageable);

    List<DeliveryRequestItemDTO> dtoList = convertToDTOList(result.getContent());

    return createPageResponseDTO(pageRequestDTO, dtoList, result.getTotalElements());
  }

  // DTO 변환 메서드
  private List<DeliveryRequestItemDTO> convertToDTOList(List<DeliveryRequestItem> items) {
    return items.stream()
            .map(item -> DeliveryRequestItemDTO.builder()
                    .drItemId(item.getDrItemId())
                    .drId(item.getDeliveryRequest().getDrId())
                    .drItemCode(item.getDrItemCode())
                    .drItemQty(item.getDrItemQty())
                    .drItemDueDate(item.getDrItemDueDate())
                    .cstorageId(item.getCompanyStorage().getCstorageId())
                    .creDate(item.getCreDate())
                    .orderId(item.getDeliveryRequest().getOrdering().getOrderId())
                    .matName(item.getDeliveryRequest().getOrdering().getContractMaterial().getMaterial().getMatName())
                    .orderEnd(item.getDeliveryRequest().getOrdering().getOrderEnd())
                    .orderQty(item.getDeliveryRequest().getOrdering().getOrderQty())
                    .build())
            .collect(Collectors.toList());
  }

  // PageResponseDTO 생성 메서드
  private PageResponseDTO<DeliveryRequestItemDTO> createPageResponseDTO(PageRequestDTO pageRequestDTO, List<DeliveryRequestItemDTO> dtoList, long totalElements) {
    return PageResponseDTO.<DeliveryRequestItemDTO>withAll()
            .pageRequestDTO(pageRequestDTO)
            .dtoList(dtoList)
            .total((int) totalElements)
            .build();
  }

  @Override
  public PageResponseDTO<DeliveryRequestItemDTO> listWithDeliveryRequestItem(PageRequestDTO pageRequestDTO) {

    // 검색 조건을 받아옵니다.
    String drItemCode = pageRequestDTO.getDrItemCode();
    String orderId = pageRequestDTO.getOrderId();
    String matName = pageRequestDTO.getMatName();
    LocalDate orderEndStart = pageRequestDTO.getOrderEndStart();
    LocalDate orderEndEnd = pageRequestDTO.getOrderEndEnd();
    LocalDate drItemDueDateStart = pageRequestDTO.getDrItemDueDateStart();
    LocalDate drItemDueDateEnd = pageRequestDTO.getDrItemDueDateEnd();
    LocalDate creDateStart = pageRequestDTO.getCreDateStart();
    LocalDate creDateEnd = pageRequestDTO.getCreDateEnd();

    // Pageable을 pageRequestDTO에서 받아옵니다.
    Pageable pageable = pageRequestDTO.getPageable("drItemId");

    // 검색 조건과 페이지 정보를 이용하여 데이터를 조회합니다.
    Page<DeliveryRequestItem> result = deliveryRequestItemRepository
            .searchDeliveryRequestItemAll(drItemCode, orderId, matName, orderEndStart, orderEndEnd,
                    drItemDueDateStart, drItemDueDateEnd, creDateStart, creDateEnd, pageable);

    // 조회된 데이터(DeliveryRequest)를 DeliveryRequestDTO로 변환합니다.
    List<DeliveryRequestItemDTO> dtoList = new ArrayList<>();



    for (DeliveryRequestItem drItem : result.getContent()) {

      Ordering ordering = drItem.getDeliveryRequest().getOrdering();

      // DTO 생성
      DeliveryRequestItemDTO dto = DeliveryRequestItemDTO.builder()
              .drItemId(drItem.getDrItemId())
              .drItemCode(drItem.getDrItemCode())
              .orderId(ordering.getOrderId())
              .matName(ordering.getContractMaterial().getMaterial().getMatName())
              .orderEnd(ordering.getOrderEnd())
              .orderQty(ordering.getOrderQty())
              .drItemDueDate(drItem.getDrItemDueDate())
              .drItemQty(drItem.getDrItemQty())
              .creDate(drItem.getCreDate())
              .cstorageId(drItem.getCompanyStorage().getCstorageId())
              .build();

      dtoList.add(dto);
    }

    // PageResponseDTO로 변환하여 반환합니다.
    return PageResponseDTO.<DeliveryRequestItemDTO>withAll()
            .pageRequestDTO(pageRequestDTO)
            .dtoList(dtoList)
            .total((int) result.getTotalElements())
            .build();
  }

}
