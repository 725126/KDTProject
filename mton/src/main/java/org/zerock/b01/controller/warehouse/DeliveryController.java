package org.zerock.b01.controller.warehouse;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/internal/delivery")
public class DeliveryController {
    // 납입지시 등록
    @GetMapping("/instruction")
    public String instructionGet() {
        return "/page/warehouse/delivery/instruction";
    }

    // 납입지시 요청
    @GetMapping("/request")
    public String requestGet() {
        return "/page/warehouse/delivery/request";
    }

    // 납입지시 조회
    @GetMapping("/status")
    public String statusGet() {
        return "/page/warehouse/delivery/status";
    }
}
