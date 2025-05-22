package org.zerock.b01.service.warehouse;

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
}
