package org.zerock.b01.repository.warehouse;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.b01.domain.operation.Ordering;
import org.zerock.b01.domain.warehouse.DeliveryRequest;
import org.zerock.b01.repository.search.warehouse.DeliveryRequestSearch;

import java.util.List;
import java.util.Optional;

public interface DeliveryRequestRepository extends JpaRepository<DeliveryRequest,Long>, DeliveryRequestSearch {

  Optional<DeliveryRequest> findByOrdering_OrderId(String orderId);

  List<DeliveryRequest> findByOrderingOrderIdIn(List<String> orderIds);

  List<DeliveryRequest> findByOrdering(Ordering ordering);

}
