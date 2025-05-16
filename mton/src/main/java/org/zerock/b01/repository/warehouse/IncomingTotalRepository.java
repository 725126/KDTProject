package org.zerock.b01.repository.warehouse;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.b01.domain.warehouse.DeliveryRequestItem;
import org.zerock.b01.domain.warehouse.IncomingTotal;
import org.zerock.b01.repository.search.warehouse.IncomingTotalSearch;

import java.util.Optional;

public interface IncomingTotalRepository extends JpaRepository<IncomingTotal, Long>, IncomingTotalSearch {

  Optional<IncomingTotal> findByDeliveryRequestItem(DeliveryRequestItem deliveryRequestItem);

  Optional<IncomingTotal> findByDeliveryRequestItem_drItemId(Long drItemId);
}
