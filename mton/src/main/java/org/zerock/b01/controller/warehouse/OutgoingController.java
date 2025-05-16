package org.zerock.b01.controller.warehouse;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/internal/outgoing")
public class OutgoingController {
    // 출고 조회
    @GetMapping("/status")
    public String statusGet() {
        return "page/warehouse/outgoing/status";
    }

    // 출고 이력
    @GetMapping("/history")
    public String historyGet() {
        return "page/warehouse/outgoing/history";
    }

    // 출고 처리 결과 조회
    @GetMapping("/result")
    public String resultGet() {
        return "page/warehouse/outgoing/result";
    }
}
