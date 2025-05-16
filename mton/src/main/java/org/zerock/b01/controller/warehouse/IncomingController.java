package org.zerock.b01.controller.warehouse;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.zerock.b01.domain.warehouse.CompanyStorage;
import org.zerock.b01.domain.warehouse.IncomingTotal;
import org.zerock.b01.dto.PageRequestDTO;
import org.zerock.b01.dto.PageResponseDTO;
import org.zerock.b01.dto.warehouse.*;
import org.zerock.b01.service.warehouse.IncomingItemService;
import org.zerock.b01.service.warehouse.IncomingService;
import org.zerock.b01.service.warehouse.IncomingTotalService;

import java.util.List;

@Controller("incomingWarehouseController")
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/internal/incoming")
public class IncomingController { // 입고

    private final IncomingTotalService incomingTotalService;
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

    // 입고 검수
    @GetMapping("/inspection")
    public String listIncomingInspection(PageRequestDTO pageRequestDTO, Model model) {

        // 전체 납입지시 상세 항목 페이징 조회
        PageResponseDTO<IncomingInspectionDTO> incomingInspectionList =
                incomingService.listWithIncomingInspection(pageRequestDTO);

        model.addAttribute("incomingInspectionList", incomingInspectionList.getDtoList());
        model.addAttribute("pageRequestDTO", pageRequestDTO);
        model.addAttribute("totalCount", incomingInspectionList.getTotal());

        return "/page/warehouse/incoming/inspection";
    }

    @GetMapping("/close")
    public String readIncomingClose(Long drItemId, PageRequestDTO pageRequestDTO, Model model) {

        IncomingTotalDTO incomingTotalDTO = incomingTotalService.readIncomingTotalOne(drItemId);
        model.addAttribute("dto", incomingTotalDTO);

        Long incomingTotalId = incomingTotalDTO.getIncomingTotalId();
        PageResponseDTO<IncomingInspectionDTO> closeList =
                incomingService.getListIncomingWithTotal(incomingTotalId, pageRequestDTO);

        model.addAttribute("closeList", closeList.getDtoList());
        model.addAttribute("pageRequestDTO", pageRequestDTO);
        model.addAttribute("totalCount", closeList.getTotal());

        return "/page/warehouse/incoming/close";

    }

    // 입고 이력
    @GetMapping("/history")
    public String listIncomingItem(PageRequestDTO pageRequestDTO, Model model) {

        // 전체 납입지시 상세 항목 페이징 조회
        PageResponseDTO<IncomingItemDTO> incomingItemList =
                incomingItemService.listWithIncomingItem(pageRequestDTO);

        model.addAttribute("incomingItemList", incomingItemList.getDtoList());
        model.addAttribute("pageRequestDTO", pageRequestDTO);
        model.addAttribute("totalCount", incomingItemList.getTotal());

        return "/page/warehouse/incoming/history";
    }

    // 입고 현황 조회
    @GetMapping("/status")
    public String listIncomingTotal(PageRequestDTO pageRequestDTO, Model model) {

        // 전체 납입지시 상세 항목 페이징 조회
        PageResponseDTO<IncomingTotalDTO> incomingTotalList =
                incomingTotalService.listWithIncomingTotal(pageRequestDTO);

        model.addAttribute("incomingTotalList", incomingTotalList.getDtoList());
        model.addAttribute("pageRequestDTO", pageRequestDTO);
        model.addAttribute("totalCount", incomingTotalList.getTotal());

        return "/page/warehouse/incoming/status";
    }
}
