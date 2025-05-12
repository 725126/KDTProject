package org.zerock.b01.service.warehouse;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.zerock.b01.domain.warehouse.DeliveryPartnerItem;
import org.zerock.b01.domain.warehouse.Incoming;
import org.zerock.b01.domain.warehouse.IncomingStatus;
import org.zerock.b01.dto.PageRequestDTO;
import org.zerock.b01.dto.PageResponseDTO;
import org.zerock.b01.dto.warehouse.IncomingDTO;
import org.zerock.b01.repository.warehouse.IncomingRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class IncomingServiceImpl implements IncomingService {

  private final IncomingRepository incomingRepository;

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
            .incomingCode(incomingCode)
            .incomingQty(0)
            .incomingReturnQty(0)
            .incomingStatus(IncomingStatus.미입고)
            .build();

    incomingRepository.save(incoming);
  }

  @Override
  public PageResponseDTO<IncomingDTO> listWithIncoming(PageRequestDTO pageRequestDTO) {

    // 검색 조건을 받아옵니다.
    LocalDate deliveryPartnerItemDateStart = pageRequestDTO.getDeliveryPartnerItemDateStart();
    LocalDate deliveryPartnerItemDateEnd = pageRequestDTO.getDeliveryPartnerItemDateEnd();
    String incomingCode = generateIncomingCode();
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
}
