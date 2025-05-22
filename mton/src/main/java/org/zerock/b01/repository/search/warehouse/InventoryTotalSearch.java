package org.zerock.b01.repository.search.warehouse;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.zerock.b01.domain.warehouse.InventoryTotal;

public interface InventoryTotalSearch {

  Page<InventoryTotal> searchInventoryByMaterial(String matId, String matType, String matName, Pageable pageable);
}
