package org.zerock.b01.service.warehouse;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.zerock.b01.domain.operation.Material;
import org.zerock.b01.domain.warehouse.CompanyStorage;
import org.zerock.b01.domain.warehouse.Inventory;
import org.zerock.b01.domain.warehouse.InventoryTotal;
import org.zerock.b01.dto.warehouse.InventoryDTO;
import org.zerock.b01.repository.warehouse.*;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class InventoryServiceImpl implements InventoryService {

  private final InventoryRepository inventoryRepository;
  private final InventoryTotalRepository inventoryTotalRepository;
  private final OutgoingRepository outgoingRepository;
  private final InventoryTotalService inventoryTotalService;

  // 재고 조회하거나 없으면 기본값으로 신규 생성 후 반환
  @Override
  public Inventory getOrCreateInventory(CompanyStorage companyStorage, Material material) {

    InventoryTotal inventoryTotal = inventoryTotalService.getOrCreateInventoryTotal(material);

    return inventoryRepository.findByCompanyStorageAndMaterial(companyStorage, material)
            .orElseGet(() -> {
              Inventory newInventory = Inventory.builder()
                      .companyStorage(companyStorage)
                      .material(material)
                      .inventoryTotal(inventoryTotal)
                      .totalQty(0)
                      .totalPrice(0L)
                      .build();
              return inventoryRepository.save(newInventory); // 저장 추가
            });
  }

  // 입고시 재고 수량, 금액 누적 변경 및 저장
  @Override
  public Inventory addQtyAndSave(Inventory inventory, int changeQty, Long changePrice) {
    inventory.addQty(changeQty, changePrice);

    InventoryTotal inventoryTotal = inventory.getInventoryTotal();
    inventoryTotal.addQty(changeQty, changePrice);

    // 각각 저장
    inventoryTotalRepository.save(inventoryTotal);

    return inventoryRepository.save(inventory);
  }

  // 출고시 재고 수량, 금액 누적 변경 및 저장
  @Override
  public Inventory subtractQtyAndSave(Inventory inventory, int changeQty, Long changePrice) {
    inventory.subtractQty(changeQty, changePrice);

    InventoryTotal inventoryTotal = inventory.getInventoryTotal();
    inventoryTotal.subtractQty(changeQty, changePrice);

    // 각각 저장
    inventoryTotalRepository.save(inventoryTotal);

    return inventoryRepository.save(inventory);
  }

  @Override
  public List<InventoryDTO> findAllInventoryDTOs() {
    List<Inventory> inventories = inventoryRepository.findAll();

    return inventories.stream().map(inv -> {
      int totalQty = inv.getTotalQty();

      // 🔹 해당 inventoryId로 아직 출고 마감 안 된 출고 수량 합계 조회
      int reservedQty = outgoingRepository.sumUnclosedOutgoingQtyByInventoryId(inv.getInventoryId())
              .orElse(0); // null 방지

      int availableQty = totalQty - reservedQty;

      return new InventoryDTO(
              inv.getInventoryId(),
              inv.getMaterial().getMatId(),
              inv.getCompanyStorage().getCstorageId(),
              totalQty,
              availableQty
      );
    }).collect(Collectors.toList());
  }

}
