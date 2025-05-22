package org.zerock.b01.repository.warehouse;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.b01.domain.operation.Material;
import org.zerock.b01.domain.warehouse.InventoryTotal;
import org.zerock.b01.repository.search.warehouse.InventoryTotalSearch;

import java.util.Optional;

public interface InventoryTotalRepository extends JpaRepository<InventoryTotal, Long>, InventoryTotalSearch {

  Optional<InventoryTotal> findByMaterial(Material material);;
}
