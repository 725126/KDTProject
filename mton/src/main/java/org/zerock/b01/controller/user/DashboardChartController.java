package org.zerock.b01.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zerock.b01.security.CustomUserDetails;
import org.zerock.b01.service.warehouse.DashboardService;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class DashboardChartController {
    private final DashboardService dashboardChartService;

    @GetMapping("/internal/api/chart/inout")
    public ResponseEntity<?> getWeeklyInOutChartData() {
        return ResponseEntity.ok(dashboardChartService.getWeeklyInOutChartData());
    }

    @GetMapping("/internal/api/chart/stock")
    public ResponseEntity<?> getWeeklyStockChart() {
        return ResponseEntity.ok(dashboardChartService.getWeeklyStockAmountTrend());
    }

    @GetMapping("/internal/api/chart/calendar/events")
    public ResponseEntity<List<Map<String, Object>>> getCalendarEvents() {
        return ResponseEntity.ok(dashboardChartService.getCalendarEvents());
    }

    @GetMapping("/external/api/chart/delivery")
    public ResponseEntity<?> getDeliveryTrendChart(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(dashboardChartService.getDeliveryTrendChart(userDetails.getUser()));
    }

    @GetMapping("/external/api/chart/on-time-rate")
    public ResponseEntity<?> getMonthlyOnTimeRate(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(dashboardChartService.getMonthlyOnTimeRate(userDetails.getUser()));
    }

    @GetMapping("/external/api/calendar/events")
    public ResponseEntity<List<Map<String, Object>>> getPartnerCalendarEvents(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(dashboardChartService.getPartnerCalendarEvents(userDetails.getUser()));
    }
}
