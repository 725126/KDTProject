package org.zerock.b01.repository.warehouse;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.zerock.b01.domain.warehouse.DeliveryPartnerItem;

public interface DeliveryPartnerItemRepository extends JpaRepository<DeliveryPartnerItem,Long> {

  // 오늘 날짜 기준 입고 예정 건수
  @Query("SELECT COUNT(dpi) FROM DeliveryPartnerItem dpi " +
          "WHERE FUNCTION('DATE', dpi.deliveryPartnerItemDate) = CURRENT_DATE")
  Long countTodayIncoming();
}
