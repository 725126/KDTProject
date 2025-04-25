package org.zerock.b01.controller.warehouse;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller("incomingWarehouseController")
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/internal/incoming")
public class IncomingController { // 입고

    // 입고 예정
    @GetMapping("/planned")
    public String plannedGet() {
        return "/page/warehouse/incoming/planned";
    }

    // 입고 검수
    @GetMapping("/inspection")
    public String inspectionGet() {
        return "/page/warehouse/incoming/inspection";
    }

    // 입고 이력
    @GetMapping("/history")
    public String historyGet() {
        return "/page/warehouse/incoming/history";
    }

    // 입고 현황 조회
    @GetMapping("/status")
    public String statusGet() {
        return "/page/warehouse/incoming/status";
    }
}
