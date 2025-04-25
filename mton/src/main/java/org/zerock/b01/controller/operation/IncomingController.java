package org.zerock.b01.controller.operation;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller("incomingOperationController")
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/internal/incoming")
public class IncomingController { // 입고

    // 거래 명세
    @GetMapping("/trans")
    public String transGet() {
        return "/page/operation/incoming/trans";
    }
}
