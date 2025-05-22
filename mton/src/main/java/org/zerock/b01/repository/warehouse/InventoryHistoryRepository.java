package org.zerock.b01.repository.warehouse;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.b01.domain.warehouse.InventoryHistory;
import org.zerock.b01.repository.search.warehouse.InventoryHistorySearch;

public interface InventoryHistoryRepository extends JpaRepository<InventoryHistory, Long>, InventoryHistorySearch {
}
