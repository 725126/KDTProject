package org.zerock.b01.repository.warehouse;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.b01.domain.warehouse.OutgoingItem;

public interface OutgoingItemRepository extends JpaRepository<OutgoingItem, Long> {
}
