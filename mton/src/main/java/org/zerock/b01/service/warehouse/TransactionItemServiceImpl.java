package org.zerock.b01.service.warehouse;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.zerock.b01.domain.operation.Ordering;
import org.zerock.b01.domain.operation.TransactionItem;
import org.zerock.b01.domain.warehouse.IncomingTotal;
import org.zerock.b01.dto.PageRequestDTO;
import org.zerock.b01.dto.PageResponseDTO;
import org.zerock.b01.dto.warehouse.TransactionViewDTO;
import org.zerock.b01.repository.warehouse.IncomingTotalRepository;
import org.zerock.b01.repository.warehouse.TransactionItemRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class TransactionItemServiceImpl implements TransactionItemService {

  private final TransactionItemRepository transactionItemRepository;
  private final IncomingTotalRepository incomingTotalRepository;

  @Override
  public PageResponseDTO<TransactionViewDTO> listWithTransactionItem(PageRequestDTO pageRequestDTO) {

    // 검색 조건을 받아옵니다.
    String orderId = pageRequestDTO.getOrderId();
    String pCompany = pageRequestDTO.getPCompany();
    String matId = pageRequestDTO.getMatId();
    String matName = pageRequestDTO.getMatName();

    // Pageable을 pageRequestDTO에서 받아옵니다.
    Pageable pageable = pageRequestDTO.getPageable("orderId");

    // 검색 조건과 페이지 정보를 이용하여 데이터를 조회합니다.
    Page<TransactionViewDTO> result = transactionItemRepository
            .searchTransactionItem(orderId, pCompany, matId, matName, pageable);

    List<TransactionViewDTO> dtoList = result.stream()
            .map(item -> {
              // drId로 lastCompletedAt 조회 (주문ID 또는 발주ID 역할로 판단)
              LocalDateTime lastCompletedAtDateTime = incomingTotalRepository
                      .findLatestIncomingCompletedAtByOrderIdAndOrderStatCompleted(item.getOrderId());
              LocalDate lastCompletedAt = lastCompletedAtDateTime != null ? lastCompletedAtDateTime.toLocalDate() : null;

              // Builder로 새 DTO 생성 (기존 필드 복사 + 추가 필드 세팅)
              return TransactionViewDTO.builder()
                      .orderId(item.getOrderId())
                      .pCompany(item.getPCompany())
                      .matId(item.getMatId())
                      .matName(item.getMatName())
                      .titemPrice(item.getTitemPrice())
                      .titemQty(item.getTitemQty())
                      .amount(item.getAmount())
                      .incomingLastCompletedAt(lastCompletedAt)
                      .build();
            }).toList();

    // PageResponseDTO로 변환하여 반환합니다.
    return PageResponseDTO.<TransactionViewDTO>withAll()
            .pageRequestDTO(pageRequestDTO)
            .dtoList(dtoList)
            .total((int) result.getTotalElements())
            .build();
  }

  public void createTransactionItemsForOrdering(Ordering ordering) {
    List<IncomingTotal> incomingTotals = incomingTotalRepository.findByDeliveryRequestItem_DeliveryRequest_Ordering(ordering);

    int totalQty = incomingTotals.stream()
            .mapToInt(IncomingTotal::getIncomingEffectiveQty)
            .sum();

    // 가격 등은 계약자재(ContractMaterial) 기준이라 가정
    int price = ordering.getContractMaterial().getCmtPrice();

    TransactionItem item = TransactionItem.builder()
            .ordering(ordering)
            .matId(ordering.getContractMaterial().getMaterial().getMatId())
            .matName(ordering.getContractMaterial().getMaterial().getMatName())
            .titemPrice(price)
            .titemQty(totalQty)
            .amount(price * totalQty)
            .remark("-")
            .build();

    transactionItemRepository.save(item);
  }
}