package org.zerock.b01.service.warehouse;

import org.zerock.b01.domain.operation.Material;
import org.zerock.b01.domain.warehouse.CompanyStorage;
import org.zerock.b01.domain.warehouse.Inventory;
import org.zerock.b01.dto.warehouse.InventoryDTO;

import java.util.List;

public interface InventoryService {

  Inventory getOrCreateInventory(CompanyStorage companyStorage, Material material);

  Inventory addQtyAndSave(Inventory inventory, int changeQty, Long changePrice);

  List<InventoryDTO> findAllInventoryDTOs();

  Inventory subtractQtyAndSave(Inventory inventory, int changeQty, Long changePrice);

}
