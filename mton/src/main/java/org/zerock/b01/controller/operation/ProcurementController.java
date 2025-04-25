package org.zerock.b01.controller.operation;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/internal/procurement")
public class ProcurementController {
    //조달 계획
    @GetMapping("/procure")
    public String procureGet() {
        return "/page/operation/procurement/procure";
    }

    // 계약 정보
    @GetMapping("/contract")
    public String contractGet() {
        return "/page/operation/procurement/contract";
    }

    // 자재 발주
    @GetMapping("/order")
    public String orderGet() {
        return "/page/operation/procurement/order";
    }

    // 진척 검수
    @GetMapping("/inspect")
    public String inspectGet() {
        return "/page/operation/procurement/inspect";
    }


}
