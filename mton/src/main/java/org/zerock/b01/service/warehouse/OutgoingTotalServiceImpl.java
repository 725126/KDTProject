package org.zerock.b01.service.warehouse;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.zerock.b01.controller.operation.repository.PbomRepository;
import org.zerock.b01.domain.operation.Material;
import org.zerock.b01.domain.operation.Pbom;
import org.zerock.b01.domain.operation.ProductionPlan;
import org.zerock.b01.domain.operation.StatusTuple;
import org.zerock.b01.domain.warehouse.OutgoingStatus;
import org.zerock.b01.domain.warehouse.OutgoingTotal;
import org.zerock.b01.dto.PageRequestDTO;
import org.zerock.b01.dto.PageResponseDTO;
import org.zerock.b01.dto.warehouse.OutgoingTotalDTO;
import org.zerock.b01.repository.warehouse.OutgoingTotalRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class OutgoingTotalServiceImpl implements OutgoingTotalService {

  private final OutgoingTotalRepository outgoingTotalRepository;
  private final PbomRepository pbomRepository;

  @Override
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

  public boolean canDeleteByProductionPlanId(String prdplanId) {
    // 생산계획에 해당하는 출고 총 목록을 가져옴
    List<OutgoingTotal> outgoingTotals = outgoingTotalRepository.findAllByProductionPlanPrdplanId(prdplanId);

    // 출고 상태가 '출고대기'가 아닌 것이 하나라도 있으면 false 반환
    for (OutgoingTotal ot : outgoingTotals) {
      if (!ot.getOutgoingStatus().equals(OutgoingStatus.출고대기)) {
        return false;
      }
    }

    // 모두 '출고대기' 상태일 때만 true 반환
    return true;
  }

  public StatusTuple validateDeletePlans(List<String> prdplanIds) {
    for (String prdplanId : prdplanIds) {
      if (!canDeleteByProductionPlanId(prdplanId)) {
        return new StatusTuple(false,
                "생산계획 ID " + prdplanId + " 에는 출고가 진행 중인 항목이 있어 삭제할 수 없습니다.");
      }
    }
    return new StatusTuple(true, "삭제 가능한 생산계획입니다.");
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

    // Pageable을 pageRequestDTO에서 받아옵니다.
    Pageable pageable = pageRequestDTO.getPageable("outgoingTotalId");

    // 검색 조건과 페이지 정보를 이용하여 데이터를 조회합니다.
    Page<OutgoingTotal> result = outgoingTotalRepository
            .searchOutgoingTotal(prdplanEndStart, prdplanEndEnd,
                    prdplanId, matId, matName, outgoingStatus, pageable);

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
}
