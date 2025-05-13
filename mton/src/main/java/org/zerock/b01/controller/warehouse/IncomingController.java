package org.zerock.b01.controller.warehouse;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.zerock.b01.domain.warehouse.Incoming;
import org.zerock.b01.dto.PageRequestDTO;
import org.zerock.b01.dto.PageResponseDTO;
import org.zerock.b01.dto.warehouse.DeliveryRequestItemDTO;
import org.zerock.b01.dto.warehouse.IncomingDTO;
import org.zerock.b01.dto.warehouse.IncomingItemDTO;
import org.zerock.b01.service.warehouse.IncomingItemService;
import org.zerock.b01.service.warehouse.IncomingService;

@Controller("incomingWarehouseController")
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/internal/incoming")
public class IncomingController { // 입고

    private final IncomingService incomingService;
    private final IncomingItemService incomingItemService;

    //입고 예정 페이지
    @GetMapping("/planned")
    public String listIncoming(PageRequestDTO pageRequestDTO, Model model) {

        // 전체 납입지시 상세 항목 페이징 조회
        PageResponseDTO<IncomingDTO> incomingList =
                incomingService.listWithIncoming(pageRequestDTO);

        model.addAttribute("incomingList", incomingList.getDtoList());
        model.addAttribute("pageRequestDTO", pageRequestDTO);
        model.addAttribute("totalCount", incomingList.getTotal());

        return "/page/warehouse/incoming/planned";
    }

//    // 입고 검수
//    @GetMapping("/inspection")
//    public String listIncomingItems(PageRequestDTO pageRequestDTO, Model model) {
//
//        // 전체 납입지시 상세 항목 페이징 조회
//        PageResponseDTO<IncomingItemDTO> incomingItemList =
//                incomingItemService.listWithIncomingItem(pageRequestDTO);
//
//        model.addAttribute("incomingItemList", incomingItemList.getDtoList());
//        model.addAttribute("pageRequestDTO", pageRequestDTO);
//        model.addAttribute("totalCount", incomingItemList.getTotal());
//
//        return "/page/warehouse/incoming/inspection";
//    }

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
