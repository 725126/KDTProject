package org.zerock.b01.controller.partner;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/external")
public class PartnerController {
    // 계약 정보 열람
    @GetMapping("/contract/view")
    public String contractViewGet() {
        return "/page/partner/contract-view";
    }

    // 계약 자재 등록
    @GetMapping("/contmat")
    public String contmatGet() {
        return "/page/partner/contmat";
    }

    // 진척 검수 수행
    @GetMapping("/inspect")
    public String inspectGet() {
        return "/page/partner/inspect";
    }

    // 자재 재고 관리
    @GetMapping("/mat/inventory")
    public String matInventoryGet() {
        return "/page/partner/mat-inventory";
    }

    // 거래 명세 활용
    @GetMapping("/trans")
    public String transGet() {
        return "/page/partner/trans";
    }

    // 납품 지시 요청
    @GetMapping("/delivery")
    public String deliveryGet() {
        return "/page/partner/delivery";
    }
}
