package org.zerock.b01.service.warehouse;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.zerock.b01.controller.operation.repository.ProductionPlanRepository;
import org.zerock.b01.repository.warehouse.DeliveryPartnerItemRepository;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional
public class DashboardServiceImpl implements DashboardService {

  private final DeliveryPartnerItemRepository deliveryPartnerItemRepository;
  private final ProductionPlanRepository productionPlanRepository;

  @Override
  public Long getTodayIncomingCount() {
    return deliveryPartnerItemRepository.countTodayIncoming();
  }

  @Override
  public Long getTodayPlannedCount(){
    return productionPlanRepository.getTodayPlannedCount(LocalDate.now());
  }
}
