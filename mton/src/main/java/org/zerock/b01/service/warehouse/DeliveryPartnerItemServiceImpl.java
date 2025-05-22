package org.zerock.b01.service.warehouse;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.zerock.b01.domain.warehouse.DeliveryPartner;
import org.zerock.b01.domain.warehouse.DeliveryPartnerItem;
import org.zerock.b01.repository.warehouse.DeliveryPartnerItemRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional
public class DeliveryPartnerItemServiceImpl implements DeliveryPartnerItemService {

  private final DeliveryPartnerItemRepository deliveryPartnerItemRepository;

  @Override
  public DeliveryPartnerItem saveDeliveryPartnerItem(DeliveryPartner deliveryPartner, int deliveredQty) {
    // 이력 저장
    DeliveryPartnerItem item = DeliveryPartnerItem.builder()
            .deliveryPartner(deliveryPartner)
            .deliveryPartnerItemQty(deliveredQty)
            .deliveryPartnerItemDate(LocalDateTime.now())
            .build();

    return deliveryPartnerItemRepository.save(item);
  }

}
