package org.zerock.b01.service.warehouse;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.zerock.b01.controller.operation.repository.ContractRepository;
import org.zerock.b01.controller.operation.repository.InspectionRepository;
import org.zerock.b01.controller.operation.repository.OrderingRepository;
import org.zerock.b01.controller.operation.repository.ProductionPlanRepository;
import org.zerock.b01.domain.operation.Contract;
import org.zerock.b01.domain.operation.Inspection;
import org.zerock.b01.domain.operation.Ordering;
import org.zerock.b01.domain.user.Partner;
import org.zerock.b01.domain.user.User;
import org.zerock.b01.domain.warehouse.*;
import org.zerock.b01.repository.user.PartnerRepository;
import org.zerock.b01.repository.warehouse.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional
public class DashboardServiceImpl implements DashboardService {

  private final DeliveryPartnerItemRepository deliveryPartnerItemRepository;
  private final ProductionPlanRepository productionPlanRepository;
  private final OrderingRepository orderingRepository;
  private final IncomingItemRepository incomingItemRepository;
  private final OutgoingRepository outgoingRepository;
  private final InventoryHistoryRepository inventoryHistoryRepository;
  private final DeliveryRequestItemRepository deliveryRequestItemRepository;
  private final IncomingTotalRepository incomingTotalRepository;
  private final OutgoingTotalRepository outgoingTotalRepository;
  private final PartnerRepository partnerRepository;
  private final ContractRepository contractRepository;
  private final InspectionRepository inspectionRepository;
  private final TransactionRepository transactionRepository;

  @Override
  public Long getTodayIncomingCount() {
    return deliveryPartnerItemRepository.countTodayIncoming();
  }

  @Override
  public Long getTodayPlannedCount(){
    return productionPlanRepository.getTodayPlannedCount(LocalDate.now());
  }


  // 월 발주 총액
  @Override
  public int getCurrentMonthOrderTotal() {
    LocalDate now = LocalDate.now();
    Integer total = orderingRepository.getMonthlyOrderTotal(now.getYear(), now.getMonthValue());
    return total != null ? total : 0;
  }

  // 월 발주 진행률
  @Override
  public double getCurrentMonthOrderProgressRate() {
    LocalDate now = LocalDate.now();
    int year = now.getYear();
    int month = now.getMonthValue();

    long totalCount = orderingRepository.countAllByMonth(year, month);
    long completedCount = orderingRepository.countCompletedByMonth(year, month);

    if (totalCount == 0) return 0.0;

    return (double) completedCount / totalCount * 100;
  }

  // 입고 vs 출고 추이
  @Override
  public Map<String, List<Integer>> getWeeklyInOutChartData() {
    LocalDate today = LocalDate.now();
    LocalDate monday = today.with(DayOfWeek.MONDAY);
    LocalDate friday = today.with(DayOfWeek.FRIDAY);

    LocalDateTime mondayStart = monday.atStartOfDay();
    LocalDateTime fridayEnd = friday.atTime(23, 59, 59);

    Map<Integer, Integer> incomingMap = new HashMap<>();
    Map<Integer, Integer> outgoingMap = new HashMap<>();

    incomingItemRepository.getWeeklyIncomingSummary(mondayStart, fridayEnd).forEach(arr -> {
      incomingMap.put(((Number) arr[0]).intValue(), ((Number) arr[1]).intValue());
    });

    outgoingRepository.getWeeklyOutgoingSummary(mondayStart, fridayEnd).forEach(arr -> {
      outgoingMap.put(((Number) arr[0]).intValue(), ((Number) arr[1]).intValue());
    });

    List<Integer> incomingList = new ArrayList<>();
    List<Integer> outgoingList = new ArrayList<>();

    for (int day = 2; day <= 6; day++) {
      incomingList.add(incomingMap.getOrDefault(day, 0));
      outgoingList.add(outgoingMap.getOrDefault(day, 0));
    }

    return Map.of(
            "incoming", incomingList,
            "outgoing", outgoingList
    );
  }

  // 재고 금액 변화
  @Override
  public List<Integer> getWeeklyStockAmountTrend() {
    int year = LocalDate.now().getYear();

    List<Object[]> rawData = inventoryHistoryRepository.getWeeklyStockChangeAmount(year);
    Map<Integer, Long> weeklyChangeMap = new TreeMap<>(); // 정렬보장

    for (Object[] row : rawData) {
      Integer week = ((Number) row[0]).intValue();
      Long amount = ((Number) row[1]).longValue();
      weeklyChangeMap.put(week, amount);
    }

    List<Integer> weeklyTotalList = new ArrayList<>();
    long cumulative = 0;

    for (int week = 1; week <= 52; week++) {
      long change = weeklyChangeMap.getOrDefault(week, 0L);
      cumulative += change;
      weeklyTotalList.add((int) cumulative); // Chart.js용: int로 변환
    }

    return weeklyTotalList;
  }

  // 일정 캘린더
  public List<Map<String, Object>> getCalendarEvents() {
    List<Map<String, Object>> events = new ArrayList<>();

    // 🔹 예시: 발주일
//    List<Ordering> orders = orderingRepository.findAll(); // or 기간 필터
//    for (Ordering o : orders) {
//      events.add(Map.of(
//              "title", "발주 - " + o.getContractMaterial().getMaterial().getMatName(),
//              "start", o.getOrderDate().toString(),
//              "backgroundColor", "#27496D",
//              "borderColor", "#27496D",
//              "extendedProps", Map.of(
//                      "type", "발주일",
//                      "item", o.getContractMaterial().getMaterial().getMatName(),
//                      "remarks", "발주 수량: " + o.getOrderQty()
//              )
//      ));
//    }

    // 🔹 예시: 입고 예정일
    List<DeliveryRequestItem> dueList = deliveryRequestItemRepository.findAll();

    for (DeliveryRequestItem d : dueList) {
      Map<String, Object> event = new HashMap<>();
      event.put("title", "입고예정 - " + d.getDrItemCode());
      event.put("start", d.getDrItemDueDate().toString());
      event.put("backgroundColor", "#00909E");

      Map<String, Object> extendedProps = new HashMap<>();
      extendedProps.put("type", "입고 예정");
      extendedProps.put("item", d.getDrItemCode());
      extendedProps.put("remarks", "납입수량: " + d.getDrItemQty());

      event.put("extendedProps", extendedProps);

      events.add(event);
    }

    // 🔹 3. 입고 완료일
    List<IncomingTotal> incomingList = incomingTotalRepository.findByIncomingStatus(IncomingStatus.입고마감);
    for (IncomingTotal in : incomingList) {
      LocalDateTime done = in.getIncomingCompletedAt();
      if (done == null) continue;

      String itemName = in.getDeliveryRequestItem().getDrItemCode(); // 자재 코드 기준
      events.add(Map.of(
              "title", "입고완료 - " + itemName,
              "start", done.toLocalDate().toString(),
              "backgroundColor", "#45B39D",
              "extendedProps", Map.of(
                      "type", "입고 완료",
                      "item", itemName,
                      "remarks", "입고 수량: " + in.getIncomingEffectiveQty()
              )
      ));
    }

    // 🔹 4. 출고 마감일
    List<OutgoingTotal> outgoingList = outgoingTotalRepository.findByOutgoingStatus(OutgoingStatus.출고마감);
    for (OutgoingTotal out : outgoingList) {
      LocalDateTime done = out.getOutgoingCompletedAt();
      if (done == null) continue;

      String itemName = out.getMaterial().getMatName(); // 자재명 기준
      events.add(Map.of(
              "title", "출고완료 - " + itemName,
              "start", done.toLocalDate().toString(),
              "backgroundColor", "#FF9A76",
              "extendedProps", Map.of(
                      "type", "출고 완료",
                      "item", itemName,
                      "remarks", "출고 수량: " + out.getOutgoingTotalQty()
              )
      ));
    }

    return events;
  }

  // [협력업체] 계약 진행
  public int getOngoingContractCountForPartner(User user) {
    Partner partner = partnerRepository.findByUser(user)
            .orElseThrow(() -> new IllegalStateException("협력업체 정보 없음"));

    return contractRepository.countOngoingContracts(partner, LocalDate.now());
  }

  // [협력업체] 납기 준수율
  @Override
  public int getOnTimeRate(User user) {
    Partner partner = partnerRepository.findByUser(user)
            .orElseThrow(() -> new IllegalStateException("협력업체 정보 없음"));

    long total = incomingTotalRepository.countCompletedIncomingByPartner(partner);
    if (total == 0) return 0;

    long onTime = incomingTotalRepository.countOnTimeIncomingByPartner(partner);
    return (int) Math.round((double) onTime / total * 100);
  }

  // [협력업체] 검수 완료율
  @Override
   public int getInspectionCompletionRate(User user) {
    Partner partner = partnerRepository.findByUser(user)
            .orElseThrow(() -> new IllegalStateException("협력업체 정보 없음"));

    long total = inspectionRepository.countAllInspections(partner);
    if (total == 0) return 0;

    long done = inspectionRepository.countCompletedInspections(partner);
    return (int) Math.round((double) done / total * 100);
  }

  // [협력업체] 월 거래 금액
  public long getCurrentMonthTransactionAmount(User user) {
    Partner partner = partnerRepository.findByUser(user)
            .orElseThrow(() -> new IllegalStateException("협력사 정보 없음"));

    LocalDate now = LocalDate.now();
    Long total = transactionRepository.getMonthlyTransactionTotal(partner, now.getYear(), now.getMonthValue());

    return total != null ? total : 0L;
  }

  // [협력업체] 납품 완료 vs 지연 추이
  public Map<String, Object> getDeliveryTrendChart(User user) {
    Partner partner = partnerRepository.findByUser(user)
            .orElseThrow(() -> new IllegalStateException("협력사 정보 없음"));

    int year = LocalDate.now().getYear();
    List<Object[]> rawData = incomingTotalRepository.getMonthlyOnTimeAndDelayedCount(partner, year);

    Map<Integer, Integer> onTimeMap = new HashMap<>();
    Map<Integer, Integer> delayedMap = new HashMap<>();

    for (Object[] row : rawData) {
      int month = ((Number) row[0]).intValue();     // 1 ~ 12
      int onTime = ((Number) row[1]).intValue();
      int delayed = ((Number) row[2]).intValue();
      onTimeMap.put(month, onTime);
      delayedMap.put(month, delayed);
    }

    List<Integer> onTimeList = new ArrayList<>();
    List<Integer> delayedList = new ArrayList<>();

    for (int m = 1; m <= LocalDate.now().getMonthValue(); m++) {
      onTimeList.add(onTimeMap.getOrDefault(m, 0));
      delayedList.add(delayedMap.getOrDefault(m, 0));
    }

    return Map.of(
            "labels", IntStream.rangeClosed(1, LocalDate.now().getMonthValue())
                    .mapToObj(i -> i + "월")
                    .toList(),
            "onTime", onTimeList,
            "delayed", delayedList
    );
  }

  // [협력업체] 월별 납기 준수율
  @Override
  public Map<String, Object> getMonthlyOnTimeRate(User user) {
    Partner partner = partnerRepository.findByUser(user)
            .orElseThrow(() -> new IllegalStateException("협력사 정보 없음"));

    int year = LocalDate.now().getYear();
    List<Object[]> rawData = incomingTotalRepository.getMonthlyOnTimeRateData(partner, year);

    Map<Integer, Integer> totalMap = new HashMap<>();
    Map<Integer, Integer> onTimeMap = new HashMap<>();

    for (Object[] row : rawData) {
      int month = ((Number) row[0]).intValue();
      int total = ((Number) row[1]).intValue();
      int onTime = ((Number) row[2]).intValue();
      totalMap.put(month, total);
      onTimeMap.put(month, onTime);
    }

    List<String> labels = new ArrayList<>();
    List<Integer> rates = new ArrayList<>();

    for (int m = 1; m <= LocalDate.now().getMonthValue(); m++) {
      labels.add(m + "월");
      int total = totalMap.getOrDefault(m, 0);
      int onTime = onTimeMap.getOrDefault(m, 0);
      int rate = (total == 0) ? 0 : (int) Math.round((double) onTime / total * 100);
      rates.add(rate);
    }

    Map<String, Object> result = new HashMap<>();
    result.put("labels", labels);
    result.put("rates", rates);
    return result;
  }

  // [협력업체] 일정 캘린더
  @Override
  public List<Map<String, Object>> getPartnerCalendarEvents(User user) {
    Partner partner = partnerRepository.findByUser(user)
            .orElseThrow(() -> new IllegalStateException("협력업체 정보 없음"));

    List<Map<String, Object>> events = new ArrayList<>();
    LocalDate today = LocalDate.now();
    LocalDate sevenDaysLater = today.plusDays(7);

    // 1. 계약 종료 임박
    contractRepository.findExpiringContracts(partner, today, sevenDaysLater)
            .forEach(c -> events.add(Map.of(
                    "title", "계약 종료 임박",
                    "start", c.getConEnd().toString(),
                    "backgroundColor", "#FFC107",
                    "borderColor", "#FFC107",
                    "extendedProps", Map.of(
                            "type", "계약",
                            "item", c.getConId(),
                            "remarks", "계약 종료일: " + c.getConEnd()
                    )
            )));

    // 2. 납기 예정
    deliveryRequestItemRepository.findDueItemsByPartner(partner)
            .forEach(d -> events.add(Map.of(
                    "title", "납기 예정 - " + d.getDrItemCode(),
                    "start", d.getDrItemDueDate().toString(),
                    "backgroundColor", "#17A2B8",
                    "borderColor", "#17A2B8",
                    "extendedProps", Map.of(
                            "type", "납품 일정",
                            "item", d.getDrItemCode(),
                            "remarks", "납기일: " + d.getDrItemDueDate()
                    )
            )));

    // 3. 입고 완료
    incomingTotalRepository.findCompletedIncomingByPartner(partner)
            .forEach(i -> events.add(Map.of(
                    "title", "입고 완료",
                    "start", i.getIncomingCompletedAt().toLocalDate().toString(),
                    "backgroundColor", "#28A745",
                    "borderColor", "#28A745",
                    "extendedProps", Map.of(
                            "type", "입고",
                            "item", i.getDeliveryRequestItem().getDrItemCode(),
                            "remarks", "입고 수량: " + i.getIncomingEffectiveQty()
                    )
            )));
    return events;
  }

}
