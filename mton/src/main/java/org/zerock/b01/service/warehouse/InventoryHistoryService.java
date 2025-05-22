package org.zerock.b01.service.warehouse;

import org.zerock.b01.domain.warehouse.Inventory;
import org.zerock.b01.domain.warehouse.InventoryUpdateReason;
import org.zerock.b01.dto.PageRequestDTO;
import org.zerock.b01.dto.PageResponseDTO;
import org.zerock.b01.dto.warehouse.InventoryHistoryDTO;

public interface InventoryHistoryService {

  void registerHistory(Inventory inventory, String cstorageId, String matName,
                       int changeQty, Long changePrice, InventoryUpdateReason reason);

  PageResponseDTO<InventoryHistoryDTO> listWithInventoryHistory(PageRequestDTO pageRequestDTO);

}

