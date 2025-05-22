package org.zerock.b01.service.warehouse;

import org.zerock.b01.domain.operation.Material;
import org.zerock.b01.domain.warehouse.InventoryTotal;
import org.zerock.b01.dto.PageRequestDTO;
import org.zerock.b01.dto.PageResponseDTO;
import org.zerock.b01.dto.warehouse.InventoryTotalDTO;

public interface InventoryTotalService {

  InventoryTotal getOrCreateInventoryTotal(Material material);

  PageResponseDTO<InventoryTotalDTO> listWithInventoryTotal(PageRequestDTO pageRequestDTO);

}
