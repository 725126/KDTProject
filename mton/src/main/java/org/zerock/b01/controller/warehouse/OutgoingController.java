package org.zerock.b01.controller.warehouse;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.zerock.b01.domain.warehouse.OutgoingTotal;
import org.zerock.b01.dto.PageRequestDTO;
import org.zerock.b01.dto.PageResponseDTO;
import org.zerock.b01.dto.warehouse.IncomingDTO;
import org.zerock.b01.dto.warehouse.OutgoingTotalDTO;
import org.zerock.b01.service.warehouse.OutgoingTotalService;

@Controller
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/internal/outgoing")
public class OutgoingController {

    private final OutgoingTotalService outgoingTotalService;

    // 출고 조회
    @GetMapping("/status")
    public String listOutgoingTotal(PageRequestDTO pageRequestDTO, Model model) {

        // 전체 납입지시 상세 항목 페이징 조회
        PageResponseDTO<OutgoingTotalDTO> outgoingTotalList =
                outgoingTotalService.listWithOutgoingTotal(pageRequestDTO);

        model.addAttribute("outgoingTotalList", outgoingTotalList.getDtoList());
        model.addAttribute("pageRequestDTO", pageRequestDTO);
        model.addAttribute("totalCount", outgoingTotalList.getTotal());

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
