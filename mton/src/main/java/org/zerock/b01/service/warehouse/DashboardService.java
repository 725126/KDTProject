package org.zerock.b01.service.warehouse;

import org.zerock.b01.domain.user.User;

import java.util.List;
import java.util.Map;

public interface DashboardService {

  Long getTodayIncomingCount();

  Long getTodayPlannedCount();

  int getCurrentMonthOrderTotal();

  double getCurrentMonthOrderProgressRate();

  Map<String, List<Integer>> getWeeklyInOutChartData();

  List<Integer> getWeeklyStockAmountTrend();

  List<Map<String, Object>> getCalendarEvents();

  int getOngoingContractCountForPartner(User user);

  int getOnTimeRate(User user);

  int getInspectionCompletionRate(User user);

  long getCurrentMonthTransactionAmount(User user);

  Map<String, Object> getDeliveryTrendChart(User user);

  Map<String, Object> getMonthlyOnTimeRate(User user);

  List<Map<String, Object>> getPartnerCalendarEvents(User user);
}
