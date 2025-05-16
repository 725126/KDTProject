package org.zerock.b01.service.warehouse;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.zerock.b01.domain.warehouse.DeliveryPartnerItem;
import org.zerock.b01.domain.warehouse.IncomingItem;
import org.zerock.b01.dto.PageRequestDTO;
import org.zerock.b01.dto.PageResponseDTO;
import org.zerock.b01.dto.warehouse.IncomingItemDTO;
import org.zerock.b01.repository.warehouse.IncomingItemRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
@Log4j2
public class IncomingItemServiceImpl implements IncomingItemService {

  private final IncomingItemRepository incomingItemRepository;

  @Override
  public PageResponseDTO<IncomingItemDTO> listWithIncomingItem(PageRequestDTO pageRequestDTO) {

    // 검색 조건을 받아옵니다.
    LocalDate modifyStart = pageRequestDTO.getModifyDateStart();
    LocalDate modifyEnd = pageRequestDTO.getModifyDateEnd();
    String incomingCode = pageRequestDTO.getIncomingCode();
    String pCompany = pageRequestDTO.getPCompany();
    String matId = pageRequestDTO.getMatId();
    String matName = pageRequestDTO.getMatName();
    String incomingItemStatus = pageRequestDTO.getIncomingItemStatus();

    // Pageable을 pageRequestDTO에서 받아옵니다.
    Pageable pageable = pageRequestDTO.getPageable("incomingItemId");

    // 검색 조건과 페이지 정보를 이용하여 데이터를 조회합니다.
    Page<IncomingItem> result = incomingItemRepository
            .searchIncomingItem(modifyStart, modifyEnd, incomingCode,
                    pCompany, matId, matName, incomingItemStatus, pageable);

    // 조회된 데이터(DeliveryRequest)를 DeliveryRequestDTO로 변환합니다.
    List<IncomingItemDTO> dtoList = new ArrayList<>();



    for (IncomingItem incomingItem : result.getContent()) {

      DeliveryPartnerItem deliveryPartnerItem = incomingItem.getIncoming().getDeliveryPartnerItem();

      // DTO 생성
      IncomingItemDTO dto = IncomingItemDTO.builder()
              .incomingItemId(incomingItem.getIncomingItemId())
              .incomingCode(incomingItem.getIncoming().getIncomingCode())
              .modifyDate(incomingItem.getModifyDate())
              .pCompany(deliveryPartnerItem.getDeliveryPartner().getDeliveryRequestItem().getDeliveryRequest()
                      .getOrdering().getContractMaterial().getContract().getPartner().getPCompany())
              .matId(deliveryPartnerItem.getDeliveryPartner().getDeliveryRequestItem().getDeliveryRequest()
                      .getOrdering().getContractMaterial().getMaterial().getMatId())
              .matName(deliveryPartnerItem.getDeliveryPartner().getDeliveryRequestItem().getDeliveryRequest()
                      .getOrdering().getContractMaterial().getMaterial().getMatName())
              .incomingQty(incomingItem.getIncomingQty())
              .incomingReturnQty(incomingItem.getIncomingReturnQty())
              .incomingMissingQty(incomingItem.getIncomingMissingQty())
              .incomingItemStatus(incomingItem.getIncomingItemStatus().name())
              .build();

      dtoList.add(dto);
    }

    // PageResponseDTO로 변환하여 반환합니다.
    return PageResponseDTO.<IncomingItemDTO>withAll()
            .pageRequestDTO(pageRequestDTO)
            .dtoList(dtoList)
            .total((int) result.getTotalElements())
            .build();
  }
}
