package org.zerock.b01.repository.warehouse;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.b01.domain.warehouse.Inventory;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
}
