package org.zerock.b01.repository.warehouse;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.zerock.b01.domain.operation.Ordering;
import org.zerock.b01.domain.warehouse.DeliveryRequest;
import org.zerock.b01.domain.warehouse.DeliveryStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Log4j2
public class DeliveryRequestRepositoryTests {

  @Autowired
  private DeliveryRequestRepository deliveryRequestRepository;

  String oderId ="ORD20250425";
  Ordering ordering =Ordering.builder().orderId(oderId).build();

  @Test
  public void testInsertDeliveryRequest() {
    DeliveryRequest request = DeliveryRequest.builder()
            .ordering(ordering)
            .drTotalQty(100)
            .drStatus(DeliveryStatus.진행중)
            .build();

    DeliveryRequest result = deliveryRequestRepository.save(request);
    log.info("DrID: " + result.getDrId());
  }

}
