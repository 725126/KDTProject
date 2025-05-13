package org.zerock.b01.repository.warehouse;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.b01.domain.warehouse.DeliveryRequestItem;
import org.zerock.b01.domain.warehouse.IncomingTotal;
import org.zerock.b01.repository.search.warehouse.IncomingItemSearch;

import java.util.Optional;

public interface IncomingTotalRepository extends JpaRepository<IncomingTotal, Long>, IncomingItemSearch {

  Optional<IncomingTotal> findByDeliveryRequestItem(DeliveryRequestItem deliveryRequestItem);
}
