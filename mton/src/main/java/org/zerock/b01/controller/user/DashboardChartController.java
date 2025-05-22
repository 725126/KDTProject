package org.zerock.b01.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zerock.b01.service.warehouse.DashboardService;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/api/chart")
public class DashboardChartController {
    private final DashboardService dashboardChartService;

    @GetMapping("/inout")
    public ResponseEntity<?> getWeeklyInOutChartData() {
        return ResponseEntity.ok(dashboardChartService.getWeeklyInOutChartData());
    }

    @GetMapping("/stock")
    public ResponseEntity<?> getWeeklyStockChart() {
        return ResponseEntity.ok(dashboardChartService.getWeeklyStockAmountTrend());
    }

    @GetMapping("/calendar/events")
    public ResponseEntity<List<Map<String, Object>>> getCalendarEvents() {
        return ResponseEntity.ok(dashboardChartService.getCalendarEvents());
    }

}
