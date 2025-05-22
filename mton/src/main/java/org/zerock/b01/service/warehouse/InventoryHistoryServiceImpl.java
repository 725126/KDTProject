package org.zerock.b01.service.warehouse;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.zerock.b01.domain.warehouse.*;
import org.zerock.b01.dto.PageRequestDTO;
import org.zerock.b01.dto.PageResponseDTO;
import org.zerock.b01.dto.warehouse.InventoryHistoryDTO;
import org.zerock.b01.repository.warehouse.InventoryHistoryRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class InventoryHistoryServiceImpl implements InventoryHistoryService {

  private final InventoryHistoryRepository inventoryHistoryRepository;

  @Override
  public void registerHistory(Inventory inventory, String cstorageId, String matName,
                            int changeQty, Long changePrice, InventoryUpdateReason reason) {

    InventoryHistory history = InventoryHistory.builder()
            .inventory(inventory)
            .cstorageId(cstorageId)
            .matName(matName)
            .changeQty(changeQty)
            .changePrice(changePrice)
            .updateReason(reason)
            .updateDate(LocalDateTime.now())
            .build();

    inventoryHistoryRepository.save(history);
  }

  @Override
  public PageResponseDTO<InventoryHistoryDTO> listWithInventoryHistory(PageRequestDTO pageRequestDTO) {

    // 검색 조건을 받아옵니다.
    String matId = pageRequestDTO.getMatId();
    String matType = pageRequestDTO.getMatType();
    String matName = pageRequestDTO.getMatName();
    LocalDate updateDateStart = pageRequestDTO.getUpdateDateStart();
    LocalDate updateDateEnd = pageRequestDTO.getUpdateDateEnd();
    String updateReason = pageRequestDTO.getUpdateReason();

    // Pageable을 pageRequestDTO에서 받아옵니다.
    Pageable pageable = pageRequestDTO.getPageable("inventoryHistoryId");


    // 검색 조건과 페이지 정보를 이용하여 데이터를 조회합니다.
    Page<InventoryHistory> result = inventoryHistoryRepository
            .searchInventoryHistory(matId, matType, matName, updateDateStart,
                                          updateDateEnd, updateReason, pageable);

    List<InventoryHistoryDTO> dtoList = new ArrayList<>();

    for (InventoryHistory inventoryHistory : result.getContent()) {

      // DTO 생성
      InventoryHistoryDTO dto = InventoryHistoryDTO.builder()
              .inventoryHistoryId(inventoryHistory.getInventoryHistoryId())
              .matId(inventoryHistory.getInventory().getMaterial().getMatId())
              .matType(inventoryHistory.getInventory().getMaterial().getMatType())
              .matName(inventoryHistory.getInventory().getMaterial().getMatName())
              .updateDate(inventoryHistory.getUpdateDate())
              .updateReason(inventoryHistory.getUpdateReason().name())
              .changeQty(inventoryHistory.getChangeQty())
              .changePrice(inventoryHistory.getChangePrice())
              .allTotalQty(inventoryHistory.getInventory().getInventoryTotal().getAllTotalQty())
              .allTotalPrice(inventoryHistory.getInventory().getInventoryTotal().getAllTotalPrice())
              .build();

      dtoList.add(dto);
    }

    // PageResponseDTO로 변환하여 반환합니다.
    return PageResponseDTO.<InventoryHistoryDTO>withAll()
            .pageRequestDTO(pageRequestDTO)
            .dtoList(dtoList)
            .total((int) result.getTotalElements())
            .build();
  }

}
