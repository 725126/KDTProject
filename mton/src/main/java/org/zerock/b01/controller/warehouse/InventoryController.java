package org.zerock.b01.controller.warehouse;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/internal/inventory")
public class InventoryController {
    // 재고 조회
    @GetMapping("/status")
    public String statusGet() {
        return "page/warehouse/inventory/status";
    }

    // 재고 이력
    @GetMapping("/history")
    public String historyGet() {
        return "page/warehouse/inventory/history";
    }

    // 재고 금액
    @GetMapping("/price")
    public String priceGet() {
        return "page/warehouse/inventory/price";
    }
}
