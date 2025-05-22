package org.zerock.b01.service.warehouse;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.b01.controller.operation.repository.PbomRepository;
import org.zerock.b01.domain.operation.Material;
import org.zerock.b01.domain.operation.Pbom;
import org.zerock.b01.domain.operation.ProductionPlan;
import org.zerock.b01.domain.warehouse.*;
import org.zerock.b01.dto.PageRequestDTO;
import org.zerock.b01.dto.PageResponseDTO;
import org.zerock.b01.dto.warehouse.OutgoingTotalDTO;
import org.zerock.b01.repository.warehouse.InventoryRepository;
import org.zerock.b01.repository.warehouse.OutgoingRepository;
import org.zerock.b01.repository.warehouse.OutgoingTotalRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class OutgoingTotalServiceImpl implements OutgoingTotalService {

  private final InventoryService inventoryService;
  private final InventoryHistoryService inventoryHistoryService;
  private final OutgoingTotalRepository outgoingTotalRepository;
  private final InventoryRepository inventoryRepository;
  private final OutgoingRepository outgoingRepository;
  private final PbomRepository pbomRepository;

  @Override
  @Transactional
  public void createOutgoingTotalListByPrdPlan(ProductionPlan plan) {
    String prodId = plan.getProduct().getProdId();  // 상품 코드
    int planQty = plan.getPrdplanQty();  // 생산계획 수량

    List<Pbom> pbomList = pbomRepository.findAllByProdId(prodId);

    for (Pbom pbom : pbomList) {
      Material material = pbom.getMaterial();
      int requiredQty = pbom.getPbomQty();

      int estimatedQty = requiredQty * planQty;

      OutgoingTotal outgoingTotal = OutgoingTotal.builder()
              .productionPlan(plan)
              .material(material)
              .estimatedOutgoingQty(estimatedQty)
              .outgoingTotalQty(0)
              .outgoingStatus(OutgoingStatus.출고대기)
              .build();

      outgoingTotalRepository.save(outgoingTotal);
    }
  }

  @Override
  public PageResponseDTO<OutgoingTotalDTO> listWithOutgoingTotal(PageRequestDTO pageRequestDTO) {

    // 검색 조건을 받아옵니다.
    LocalDate prdplanEndStart = pageRequestDTO.getPrdplanEndStart();
    LocalDate prdplanEndEnd = pageRequestDTO.getPrdplanEndEnd();
    String prdplanId = pageRequestDTO.getPrdplanId();
    String matId = pageRequestDTO.getMatId();
    String matName = pageRequestDTO.getMatName();
    String outgoingStatus= pageRequestDTO.getOutgoingStatus();
    LocalDate outgoingCompletedAtStart = pageRequestDTO.getOutgoingCompletedAtStart();
    LocalDate outgoingCompletedAtEnd = pageRequestDTO.getOutgoingCompletedAtEnd();

    // Pageable을 pageRequestDTO에서 받아옵니다.
    Pageable pageable = pageRequestDTO.getPageable("outgoingTotalId");

    // 검색 조건과 페이지 정보를 이용하여 데이터를 조회합니다.
    Page<OutgoingTotal> result = outgoingTotalRepository
            .searchOutgoingTotal(prdplanEndStart, prdplanEndEnd,
                    prdplanId, matId, matName, outgoingStatus,
                    outgoingCompletedAtStart, outgoingCompletedAtEnd, pageable);

    // 조회된 데이터(DeliveryRequest)를 DeliveryRequestDTO로 변환합니다.
    List<OutgoingTotalDTO> dtoList = new ArrayList<>();



    for (OutgoingTotal outgoingTotal : result.getContent()) {

      // DTO 생성
      OutgoingTotalDTO dto = OutgoingTotalDTO.builder()
              .outgoingTotalId(outgoingTotal.getOutgoingTotalId())
              .outgoingFirstDate(outgoingTotal.getOutgoingFirstDate())
              .outgoingCompletedAt(outgoingTotal.getOutgoingCompletedAt())
              .prdplanId(outgoingTotal.getProductionPlan().getPrdplanId())
              .prdplanEnd(outgoingTotal.getProductionPlan().getPrdplanEnd())
              .matId(outgoingTotal.getMaterial().getMatId())
              .matName(outgoingTotal.getMaterial().getMatName())
              .estimatedOutgoingQty(outgoingTotal.getEstimatedOutgoingQty())
              .outgoingTotalQty(outgoingTotal.getOutgoingTotalQty())
              .outgoingStatus(outgoingTotal.getOutgoingStatus().name())
              .outgoingCompletedAt(outgoingTotal.getOutgoingCompletedAt())
              .build();

      dtoList.add(dto);
    }

    // PageResponseDTO로 변환하여 반환합니다.
    return PageResponseDTO.<OutgoingTotalDTO>withAll()
            .pageRequestDTO(pageRequestDTO)
            .dtoList(dtoList)
            .total((int) result.getTotalElements())
            .build();
  }

  public void updateOutgoingStatus(Long outgoingTotalId) {
    OutgoingTotal outgoingTotal = outgoingTotalRepository.findById(outgoingTotalId)
            .orElseThrow(() -> new IllegalArgumentException("해당 출고 항목이 존재하지 않습니다."));

    int expectedQty = outgoingTotal.getEstimatedOutgoingQty();
    int totalOutgoingQty = outgoingTotal.getOutgoingTotalQty();

    if (totalOutgoingQty == 0) {
      outgoingTotal.updateOutgoingStatus(OutgoingStatus.출고대기);
    } else if (totalOutgoingQty < expectedQty) {
      outgoingTotal.updateOutgoingStatus(OutgoingStatus.부분출고);
    } else if (totalOutgoingQty == expectedQty) {
      outgoingTotal.updateOutgoingStatus(OutgoingStatus.출고마감대기중);
    }

    outgoingTotalRepository.save(outgoingTotal);
  }

  @Override
  public void closeOutgoing(Long outgoingTotalId) {
    OutgoingTotal outgoingTotal = outgoingTotalRepository.findById(outgoingTotalId)
            .orElseThrow(() -> new IllegalArgumentException("해당 출고 항목이 존재하지 않습니다."));

    int totalQty = outgoingTotal.getOutgoingTotalQty();
    int expectedQty = outgoingTotal.getEstimatedOutgoingQty();

    if (totalQty != expectedQty) {
      throw new IllegalStateException("전량 출고된 경우에만 마감할 수 있습니다.");
    }

    List<Outgoing> outgoingList = outgoingRepository.findByOutgoingTotalOutgoingTotalId(outgoingTotalId);

    for (Outgoing outgoing : outgoingList) {
      int outgoingQty = outgoing.getOutgoingQty();

      Inventory inventory = outgoing.getInventory();

      CompanyStorage storage = inventory.getCompanyStorage();
      Material material = inventory.getMaterial();

      long totalInventoryQty = inventory.getTotalQty();
      long totalInventoryPrice = inventory.getTotalPrice();

      if (totalInventoryQty == 0) {
        throw new IllegalStateException("재고 수량이 0이므로 평균 단가를 계산할 수 없습니다.");
      }

      // 평균 단가
      long averagePricePerUnit = totalInventoryPrice / totalInventoryQty;

      // 출고 금액 = 평균 단가 * 출고 수량
      long changePrice = averagePricePerUnit * outgoingQty;

      // 재고 이력 등록
      inventoryHistoryService.registerHistory(
              inventory,
              storage.getCstorageId(),
              material.getMatName(),
              -outgoingQty,
              -changePrice,
              InventoryUpdateReason.출고
      );

      // 재고 차감
      inventoryService.subtractQtyAndSave(inventory, outgoingQty, changePrice);
    }

    // 출고 마감 처리
    outgoingTotal.markAsCompleted();
    outgoingTotalRepository.save(outgoingTotal);

  }

}
