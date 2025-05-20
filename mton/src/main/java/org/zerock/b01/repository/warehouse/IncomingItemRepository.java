package org.zerock.b01.repository.warehouse;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.b01.domain.warehouse.IncomingItem;
import org.zerock.b01.repository.search.warehouse.IncomingItemSearch;

import java.util.List;

public interface IncomingItemRepository extends JpaRepository<IncomingItem,Long>, IncomingItemSearch {
    List<IncomingItem> findByIncoming_IncomingTotal_IncomingTotalId(Long incomingTotalId);
}
