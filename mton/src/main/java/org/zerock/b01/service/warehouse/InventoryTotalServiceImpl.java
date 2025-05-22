package org.zerock.b01.service.warehouse;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.zerock.b01.domain.operation.Material;
import org.zerock.b01.domain.warehouse.InventoryTotal;
import org.zerock.b01.dto.PageRequestDTO;
import org.zerock.b01.dto.PageResponseDTO;
import org.zerock.b01.dto.warehouse.InventoryTotalDTO;
import org.zerock.b01.repository.warehouse.IncomingTotalRepository;
import org.zerock.b01.repository.warehouse.InventoryTotalRepository;
import org.zerock.b01.repository.warehouse.OutgoingTotalRepository;
import org.zerock.b01.repository.warehouse.PartnerStorageRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Log4j2
public class InventoryTotalServiceImpl implements InventoryTotalService {

  private final InventoryTotalRepository inventoryTotalRepository;
  private final IncomingTotalRepository incomingTotalRepository;
  private final OutgoingTotalRepository outgoingTotalRepository;
  private final PartnerStorageRepository partnerStorageRepository;

  @Override
  public InventoryTotal getOrCreateInventoryTotal(Material material) {
    return inventoryTotalRepository.findByMaterial(material)
            .orElseGet(() -> {
              InventoryTotal newTotal = InventoryTotal.builder()
                      .material(material)
                      .allTotalQty(0)
                      .allTotalPrice(0L)
                      .build();
              return inventoryTotalRepository.save(newTotal);
            });
  }

  @Override
  public PageResponseDTO<InventoryTotalDTO> listWithInventoryTotal(PageRequestDTO pageRequestDTO) {

    // 검색 조건을 받아옵니다.
    String matId = pageRequestDTO.getMatId();
    String matType = pageRequestDTO.getMatType();
    String matName = pageRequestDTO.getMatName();

    // Pageable을 pageRequestDTO에서 받아옵니다.
    Pageable pageable = pageRequestDTO.getPageable("inventoryTotalId");


    // 검색 조건과 페이지 정보를 이용하여 데이터를 조회합니다.
    Page<InventoryTotal> result = inventoryTotalRepository
            .searchInventoryByMaterial(matId, matType, matName, pageable);

    Map<String, Integer> incomingQtyMap = getUnclosedIncomingQtyMap();
    Map<String, Integer> outgoingQtyMap = getUnclosedOutgoingTotalQtyMap();
    Map<String, Integer> partnerQtyMap = getPartnerQtyMap();

    // 조회된 데이터(DeliveryRequest)를 DeliveryRequestDTO로 변환합니다.
    List<InventoryTotalDTO> dtoList = new ArrayList<>();

    for (InventoryTotal inventoryTotal : result.getContent()) {

      String currentMatId = inventoryTotal.getMaterial().getMatId();

      // DTO 생성
      InventoryTotalDTO dto = InventoryTotalDTO.builder()
              .inventoryTotalId(inventoryTotal.getInventoryTotalId())
              .matId(inventoryTotal.getMaterial().getMatId())
              .matType(inventoryTotal.getMaterial().getMatType())
              .matName(inventoryTotal.getMaterial().getMatName())
              .allTotalQty(inventoryTotal.getAllTotalQty())
              .allTotalPrice(inventoryTotal.getAllTotalPrice())
              .allIncomingEffectiveQty(incomingQtyMap.getOrDefault(currentMatId, 0))
              .allOutgoingTotalQty(outgoingQtyMap.getOrDefault(currentMatId, 0))
              .partnerQty(partnerQtyMap.getOrDefault(currentMatId, 0))
              .build();

      dtoList.add(dto);
    }

    // PageResponseDTO로 변환하여 반환합니다.
    return PageResponseDTO.<InventoryTotalDTO>withAll()
            .pageRequestDTO(pageRequestDTO)
            .dtoList(dtoList)
            .total((int) result.getTotalElements())
            .build();
  }

  public Map<String, Integer> getUnclosedIncomingQtyMap() {
    List<Object[]> rawResult = incomingTotalRepository.sumUnclosedIncomingQtyByMaterial();
    Map<String, Integer> resultMap = new HashMap<>();

    for (Object[] row : rawResult) {
      String matId = (String) row[0];
      Long sumQtyLong = (Long) row[1];
      int sumQty = (sumQtyLong != null) ? sumQtyLong.intValue() : 0;
      resultMap.put(matId, sumQty);
    }

    return resultMap;
  }

  public Map<String, Integer> getPartnerQtyMap() {
    List<Object[]> rawResult = partnerStorageRepository.sumPartnerQtyByMaterial();
    Map<String, Integer> partnerQtyMap = new HashMap<>();

    for (Object[] row : rawResult) {
      String matId = (String) row[0];
      Long sumQtyLong = (Long) row[1];
      int sumQty = sumQtyLong != null ? sumQtyLong.intValue() : 0;
      partnerQtyMap.put(matId, sumQty);
    }

    return partnerQtyMap;
  }

  public Map<String, Integer> getUnclosedOutgoingTotalQtyMap() {
    List<Object[]> rawResult = outgoingTotalRepository.sumUnclosedOutgoingTotalQtyByMaterial();
    Map<String, Integer> resultMap = new HashMap<>();

    for (Object[] row : rawResult) {
      String matId = (String) row[0];
      Long sumQtyLong = (Long) row[1];
      int sumQty = (sumQtyLong != null) ? sumQtyLong.intValue() : 0;
      resultMap.put(matId, sumQty);
    }

    return resultMap;
  }

}