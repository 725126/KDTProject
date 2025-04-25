package org.zerock.b01.controller.operation;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/internal/product")
public class ProductController {
    // 품목 등록
    @GetMapping("/pbom")
    public String pbomGet() {
        return "/page/operation/product/pbom";
    }

    // 생산 계획
    @GetMapping("/prdplan")
    public String prdplanGet() {
        return "/page/operation/product/prdplan";
    }
}
