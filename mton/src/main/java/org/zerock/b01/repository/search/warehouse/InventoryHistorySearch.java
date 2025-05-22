package org.zerock.b01.repository.search.warehouse;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.zerock.b01.domain.warehouse.InventoryHistory;

import java.time.LocalDate;

public interface InventoryHistorySearch {

  Page<InventoryHistory> searchInventoryHistory(String matId, String matType, String matName,
                                                LocalDate updateDateStart, LocalDate updateDateEnd,
                                                String updateReason, Pageable pageable);
}
