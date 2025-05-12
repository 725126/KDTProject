package org.zerock.b01.service.warehouse;

import org.zerock.b01.domain.warehouse.DeliveryPartner;
import org.zerock.b01.domain.warehouse.DeliveryPartnerItem;
import org.zerock.b01.dto.warehouse.DeliveryPartnerItemDTO;

public interface DeliveryPartnerItemService {

  DeliveryPartnerItem saveDeliveryPartnerItem(DeliveryPartner deliveryPartner, int deliveredQty);
}
