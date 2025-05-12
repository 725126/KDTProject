package org.zerock.b01.repository.warehouse;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.b01.domain.warehouse.DeliveryPartner;
import org.zerock.b01.domain.warehouse.DeliveryRequestItem;
import org.zerock.b01.repository.search.warehouse.DeliveryPartnerSearch;
import org.zerock.b01.repository.search.warehouse.DeliveryPartnerSearchImpl;

import java.util.Optional;

public interface DeliveryPartnerRepository extends JpaRepository<DeliveryPartner,Long>, DeliveryPartnerSearch {

  Optional<DeliveryPartner> findByDeliveryRequestItem(DeliveryRequestItem deliveryRequestItem);

}
