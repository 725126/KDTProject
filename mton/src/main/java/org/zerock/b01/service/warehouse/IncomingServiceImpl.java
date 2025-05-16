package org.zerock.b01.service.warehouse;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.zerock.b01.domain.warehouse.*;
import org.zerock.b01.dto.PageRequestDTO;
import org.zerock.b01.dto.PageResponseDTO;
import org.zerock.b01.dto.warehouse.IncomingDTO;
import org.zerock.b01.dto.warehouse.IncomingInspectionDTO;
import org.zerock.b01.repository.warehouse.IncomingItemRepository;
import org.zerock.b01.repository.warehouse.IncomingRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class IncomingServiceImpl implements IncomingService {

  private final IncomingRepository incomingRepository;
  private final IncomingItemRepository incomingItemRepository;
  private final IncomingTotalService incomingTotalService;

  @Override
  public String generateIncomingCode() {
    String todayPrefix = "IN-" + java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd")) + "-";
    List<String> result = incomingRepository.findIncomingTopByPrefix(todayPrefix, PageRequest.of(0, 1));
    String lastCode = result.isEmpty() ? null : result.get(0);


    int nextNumber = 1;
    if (lastCode != null) {
      String numberPart = lastCode.substring(todayPrefix.length());
      nextNumber = Integer.parseInt(numberPart) + 1;
    }

    return todayPrefix + String.format("%04d", nextNumber);
  }

  @Override
  public void createIncomingForDeliveryPartnerItem(DeliveryPartnerItem deliveryPartnerItem) {
    String incomingCode = generateIncomingCode();

    Incoming incoming = Incoming.builder()
            .deliveryPartnerItem(deliveryPartnerItem)
            .incomingTotal(deliveryPartnerItem.getDeliveryPartner().getIncomingTotal())
            .incomingCode(incomingCode)
            .incomingQty(0)
            .incomingReturnQty(0)
            .incomingMissingQty(0)
            .build();

    incomingRepository.save(incoming);
  }

  @Override
  public PageResponseDTO<IncomingDTO> listWithIncoming(PageRequestDTO pageRequestDTO) {

    // 검색 조건을 받아옵니다.
    LocalDate deliveryPartnerItemDateStart = pageRequestDTO.getDeliveryPartnerItemDateStart();
    LocalDate deliveryPartnerItemDateEnd = pageRequestDTO.getDeliveryPartnerItemDateEnd();
    String incomingCode = pageRequestDTO.getIncomingCode();
    String pCompany = pageRequestDTO.getPCompany();
    String matId = pageRequestDTO.getMatId();
    String matName = pageRequestDTO.getMatName();

    // Pageable을 pageRequestDTO에서 받아옵니다.
    Pageable pageable = pageRequestDTO.getPageable("incomingId");

    // 검색 조건과 페이지 정보를 이용하여 데이터를 조회합니다.
    Page<Incoming> result = incomingRepository
            .searchIncoming(deliveryPartnerItemDateStart, deliveryPartnerItemDateEnd, incomingCode,
                    pCompany, matId, matName, pageable);

    // 조회된 데이터(DeliveryRequest)를 DeliveryRequestDTO로 변환합니다.
    List<IncomingDTO> dtoList = new ArrayList<>();



    for (Incoming incoming : result.getContent()) {

      DeliveryPartnerItem deliveryPartnerItem = incoming.getDeliveryPartnerItem();

      // DTO 생성
      IncomingDTO dto = IncomingDTO.builder()
              .incomingId(incoming.getIncomingId())
              .deliveryPartnerItemDate(deliveryPartnerItem.getDeliveryPartnerItemDate().toLocalDate())
              .incomingCode(incoming.getIncomingCode())
              .pCompany(deliveryPartnerItem.getDeliveryPartner().getDeliveryRequestItem().getDeliveryRequest()
                      .getOrdering().getContractMaterial().getContract().getPartner().getPCompany())
              .matId(deliveryPartnerItem.getDeliveryPartner().getDeliveryRequestItem().getDeliveryRequest()
                      .getOrdering().getContractMaterial().getMaterial().getMatId())
              .matName(deliveryPartnerItem.getDeliveryPartner().getDeliveryRequestItem().getDeliveryRequest()
                      .getOrdering().getContractMaterial().getMaterial().getMatName())
              .deliveryPartnerItemQty(deliveryPartnerItem.getDeliveryPartnerItemQty())
              .cstorageId(deliveryPartnerItem.getDeliveryPartner().getDeliveryRequestItem().getCompanyStorage().getCstorageId())
              .build();

      dtoList.add(dto);
    }

    // PageResponseDTO로 변환하여 반환합니다.
    return PageResponseDTO.<IncomingDTO>withAll()
            .pageRequestDTO(pageRequestDTO)
            .dtoList(dtoList)
            .total((int) result.getTotalElements())
            .build();
  }

  @Override
  public PageResponseDTO<IncomingInspectionDTO> listWithIncomingInspection(PageRequestDTO pageRequestDTO) {

    // 검색 조건을 받아옵니다.
    LocalDate deliveryPartnerItemDateStart = pageRequestDTO.getDeliveryPartnerItemDateStart();
    LocalDate deliveryPartnerItemDateEnd = pageRequestDTO.getDeliveryPartnerItemDateEnd();
    String incomingCode = pageRequestDTO.getIncomingCode();
    String pCompany = pageRequestDTO.getPCompany();
    String matId = pageRequestDTO.getMatId();
    String matName = pageRequestDTO.getMatName();
    LocalDate incomingFirstDateStart = pageRequestDTO.getIncomingFirstDateStart();
    LocalDate incomingFirstDateEnd = pageRequestDTO.getIncomingFirstDateEnd();
    String incomingStatus = pageRequestDTO.getIncomingStatus();

    // Pageable을 pageRequestDTO에서 받아옵니다.
    Pageable pageable = pageRequestDTO.getPageable("incomingId");

    // 검색 조건과 페이지 정보를 이용하여 데이터를 조회합니다.
    Page<Incoming> result = incomingRepository
            .searchIncomingInspection(deliveryPartnerItemDateStart, deliveryPartnerItemDateEnd, incomingCode,
                    pCompany, matId, matName, incomingFirstDateStart,
                    incomingFirstDateEnd, incomingStatus, pageable);

    // 조회된 데이터(DeliveryRequest)를 DeliveryRequestDTO로 변환합니다.
    List<IncomingInspectionDTO> dtoList = new ArrayList<>();



    for (Incoming incoming : result.getContent()) {

      DeliveryPartnerItem deliveryPartnerItem = incoming.getDeliveryPartnerItem();

      // DTO 생성
      IncomingInspectionDTO dto = IncomingInspectionDTO.builder()
              .incomingId(incoming.getIncomingId())
              .drItemId(deliveryPartnerItem.getDeliveryPartner().getDeliveryRequestItem().getDrItemId())
              .deliveryPartnerItemDate(deliveryPartnerItem.getDeliveryPartnerItemDate().toLocalDate())
              .incomingCode(incoming.getIncomingCode())
              .pCompany(deliveryPartnerItem.getDeliveryPartner().getDeliveryRequestItem().getDeliveryRequest()
                      .getOrdering().getContractMaterial().getContract().getPartner().getPCompany())
              .matId(deliveryPartnerItem.getDeliveryPartner().getDeliveryRequestItem().getDeliveryRequest()
                      .getOrdering().getContractMaterial().getMaterial().getMatId())
              .matName(deliveryPartnerItem.getDeliveryPartner().getDeliveryRequestItem().getDeliveryRequest()
                      .getOrdering().getContractMaterial().getMaterial().getMatName())
              .deliveryPartnerItemQty(deliveryPartnerItem.getDeliveryPartnerItemQty())
              .incomingQty(incoming.getIncomingQty())
              .incomingReturnQty(incoming.getIncomingReturnQty())
              .incomingMissingQty(incoming.getIncomingMissingQty())
              .incomingFirstDate( incoming.getIncomingTotal().getIncomingFirstDate() != null
                      ? incoming.getIncomingTotal().getIncomingFirstDate()
                      : null)
              .cstorageId(deliveryPartnerItem.getDeliveryPartner().getDeliveryRequestItem().getCompanyStorage().getCstorageId())
              .incomingStatus(incoming.getIncomingTotal().getIncomingStatus().name())
              .build();

      dtoList.add(dto);
    }

    // PageResponseDTO로 변환하여 반환합니다.
    return PageResponseDTO.<IncomingInspectionDTO>withAll()
            .pageRequestDTO(pageRequestDTO)
            .dtoList(dtoList)
            .total((int) result.getTotalElements())
            .build();
  }

  // 부분입고
  public void partialIncoming(IncomingInspectionDTO dto) {
    validateIncomingQty(dto.getIncomingQty());

    Incoming incoming = incomingRepository.findById(dto.getIncomingId())
            .orElseThrow(() -> new IllegalArgumentException("해당 항목 없음"));

    int expectedQty = incoming.getDeliveryPartnerItem().getDeliveryPartnerItemQty();
    int missingQty = Math.max(expectedQty - dto.getIncomingQty(), 0);

    IncomingTotal total = incoming.getIncomingTotal();
    total.addToTotalAndMissingTotalQty(dto.getIncomingQty(), missingQty);

    total.markFirstIncoming();

    incoming.updateIncomingQtys(dto.getIncomingQty());

    incomingRepository.save(incoming);

    incomingTotalService.updateIncomingStatus(incoming.getDeliveryPartnerItem()
                          .getDeliveryPartner().getDeliveryRequestItem().getDrItemId());

    IncomingItem itemLog = IncomingItem.builder()
            .incoming(incoming)
            .modifyDate(LocalDateTime.now())
            .incomingQty(dto.getIncomingQty())
            .incomingMissingQty(missingQty)
            .incomingItemStatus(IncomingItemStatus.입고)
            .build();

    incomingItemRepository.save(itemLog);

  }

  // 입고
  public void fullIncoming(List<IncomingInspectionDTO> dtoList) {
    for (IncomingInspectionDTO dto : dtoList) {

      Incoming incoming = incomingRepository.findById(dto.getIncomingId())
              .orElseThrow(() -> new IllegalArgumentException("해당 항목 없음"));

      int totalQty = dto.getIncomingQty();

      validateIncomingQty(totalQty);

      int expectedQty = incoming.getDeliveryPartnerItem().getDeliveryPartnerItemQty();
      int missingQty = Math.max(expectedQty - totalQty, 0);
      int remainingQty = incoming.getRemainingQty();

      if (remainingQty <= 0) {
        throw new IllegalStateException("남은 수량이 없습니다. 입고처리 불가");
      }


      IncomingTotal total = incoming.getIncomingTotal();
      total.addToTotalAndMissingTotalQty(totalQty, missingQty);

      total.markFirstIncoming();

      incoming.updateIncomingQtyFull(totalQty);

      incomingRepository.save(incoming);

      incomingTotalService.updateIncomingStatus(incoming.getDeliveryPartnerItem()
              .getDeliveryPartner().getDeliveryRequestItem().getDrItemId());

      IncomingItem itemLog = IncomingItem.builder()
              .incoming(incoming)
              .modifyDate(LocalDateTime.now())
              .incomingQty(dto.getIncomingQty())
              .incomingItemStatus(IncomingItemStatus.입고)
              .build();

      incomingItemRepository.save(itemLog);
    }
  }

  //수정
  public void modifyIncoming(IncomingInspectionDTO dto) {
    validateIncomingQty(dto.getIncomingQty());

    Incoming incoming = incomingRepository.findById(dto.getIncomingId())
            .orElseThrow(() -> new IllegalArgumentException("해당 항목 없음"));

    int newQty = dto.getIncomingQty();
    int expectedQty = incoming.getDeliveryPartnerItem().getDeliveryPartnerItemQty();

    // 기존 입고 수량
    int oldQty = incoming.getIncomingQty();

    // 새로 계산된 미입고 수량
    int newMissingQty = Math.max(expectedQty - newQty, 0);

    IncomingTotal total = incoming.getIncomingTotal();

    // 기존 수량 제거하고 새 수량 반영
    total.addToTotalAndMissingTotalQty(-oldQty, -Math.max(expectedQty - oldQty, 0)); // 기존 수량 제거
    total.addToTotalAndMissingTotalQty(newQty, newMissingQty); // 새 수량 반영

    total.markFirstIncoming();

    // 입고 객체 업데이트
    incoming.updateIncomingQtys(newQty);

    incomingRepository.save(incoming);

    // 상태 갱신
    incomingTotalService.updateIncomingStatus(
            incoming.getDeliveryPartnerItem()
                    .getDeliveryPartner()
                    .getDeliveryRequestItem()
                    .getDrItemId()
    );

    // 로그 남기기
    IncomingItem itemLog = IncomingItem.builder()
            .incoming(incoming)
            .modifyDate(LocalDateTime.now())
            .incomingQty(newQty)
            .incomingMissingQty(newMissingQty)
            .incomingItemStatus(IncomingItemStatus.수정)
            .build();

    incomingItemRepository.save(itemLog);
  }

  // 반품 등록
  public void returnIncoming(IncomingInspectionDTO dto) {

    Incoming incoming = incomingRepository.findById(dto.getIncomingId())
            .orElseThrow(() -> new IllegalArgumentException("해당 항목 없음"));

    int returnQty = dto.getIncomingReturnQty();
    int incomingQty = incoming.getIncomingQty();
    int existingReturnQty = incoming.getIncomingReturnQty();
    int newTotalReturnQty = existingReturnQty + returnQty;

    if (returnQty <= 0) {
      throw new IllegalArgumentException("반품수량은 0보다 커야 합니다.");
    }

    if (newTotalReturnQty > incomingQty) {
      throw new IllegalArgumentException("총 반품수량은 입고수량을 초과할 수 없습니다.");
    }

    // 반품된 수량만큼 업데이트
    IncomingTotal total = incoming.getIncomingTotal();
    total.addToReturnTotalQty(returnQty);

    incoming.updateIncomingReturnQty(newTotalReturnQty);

    incomingRepository.save(incoming);

    // 상태 업데이트
    incomingTotalService.updateIncomingStatus(incoming.getDeliveryPartnerItem()
            .getDeliveryPartner().getDeliveryRequestItem().getDrItemId());

    // 반품 기록 저장
    IncomingItem returnLog = IncomingItem.builder()
            .incoming(incoming)
            .modifyDate(LocalDateTime.now())
            .incomingReturnQty(returnQty)
            .incomingItemStatus(IncomingItemStatus.반품)
            .build();

    incomingItemRepository.save(returnLog);
  }

  private void validateIncomingQty(int qty) {
    if (qty < 0) {
      throw new IllegalArgumentException("입고 수량은 음수일 수 없습니다.");
    }
  }

  @Override
  public PageResponseDTO<IncomingInspectionDTO> getListIncomingWithTotal(Long incomingTotalId,
                                                                         PageRequestDTO pageRequestDTO) {

    Pageable pageable = PageRequest.of(pageRequestDTO.getPage() <= 0? 0: pageRequestDTO.getPage() -1
            , pageRequestDTO.getSize(),
            Sort.by("incomingId").ascending());

    Page<Incoming> result = incomingRepository.listIncoming(incomingTotalId,pageable);

    List<IncomingInspectionDTO> dtoList =  result.getContent().stream()
            .map(incoming -> IncomingInspectionDTO.builder()
                    .incomingCode(incoming.getIncomingCode())
                    .incomingTotalId(incoming.getIncomingTotal().getIncomingTotalId())
                    .pCompany(incoming.getDeliveryPartnerItem().getDeliveryPartner()
                            .getDeliveryRequestItem().getDeliveryRequest().getOrdering()
                            .getContractMaterial().getContract().getPartner().getPCompany())
                    .matId(incoming.getDeliveryPartnerItem().getDeliveryPartner()
                            .getDeliveryRequestItem().getDeliveryRequest().getOrdering()
                            .getContractMaterial().getMaterial().getMatId())
                    .matName(incoming.getDeliveryPartnerItem().getDeliveryPartner()
                            .getDeliveryRequestItem().getDeliveryRequest().getOrdering()
                            .getContractMaterial().getMaterial().getMatName())
                    .incomingQty(incoming.getIncomingQty())
                    .incomingReturnQty(incoming.getIncomingReturnQty())
                    .incomingMissingQty(incoming.getIncomingMissingQty())
                    .incomingFirstDate(incoming.getIncomingTotal().getIncomingFirstDate())
                    .cstorageId(incoming.getIncomingTotal().getDeliveryRequestItem()
                            .getCompanyStorage().getCstorageId())
                    .build())
            .collect(Collectors.toList());

    return PageResponseDTO.<IncomingInspectionDTO>withAll()
            .pageRequestDTO(pageRequestDTO)
            .dtoList(dtoList)
            .total((int) result.getTotalElements())
            .build();
  }

}
