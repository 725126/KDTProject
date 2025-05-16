package org.zerock.b01.repository.warehouse;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.b01.domain.warehouse.IncomingItem;
import org.zerock.b01.repository.search.warehouse.IncomingItemSearch;

public interface IncomingItemRepository extends JpaRepository<IncomingItem,Long>, IncomingItemSearch {
}
