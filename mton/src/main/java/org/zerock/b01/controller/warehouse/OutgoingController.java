package org.zerock.b01.controller.warehouse;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.zerock.b01.domain.warehouse.Inventory;
import org.zerock.b01.domain.warehouse.OutgoingTotal;
import org.zerock.b01.dto.PageRequestDTO;
import org.zerock.b01.dto.PageResponseDTO;
import org.zerock.b01.dto.warehouse.IncomingDTO;
import org.zerock.b01.dto.warehouse.InventoryDTO;
import org.zerock.b01.dto.warehouse.OutgoingDTO;
import org.zerock.b01.dto.warehouse.OutgoingTotalDTO;
import org.zerock.b01.service.warehouse.InventoryService;
import org.zerock.b01.service.warehouse.OutgoingService;
import org.zerock.b01.service.warehouse.OutgoingTotalService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/internal/outgoing")
public class OutgoingController {

    private final OutgoingTotalService outgoingTotalService;
    private final OutgoingService outgoingService;
    private final InventoryService inventoryService;

    // 출고 조회
    @GetMapping("/status")
    public String listOutgoingStatus(PageRequestDTO pageRequestDTO, Model model) {

        // 전체 납입지시 상세 항목 페이징 조회
        PageResponseDTO<OutgoingTotalDTO> outgoingTotalList =
                outgoingTotalService.listWithOutgoingTotal(pageRequestDTO);

        model.addAttribute("outgoingTotalList", outgoingTotalList.getDtoList());
        model.addAttribute("pageRequestDTO", pageRequestDTO);
        model.addAttribute("totalCount", outgoingTotalList.getTotal());


        List<InventoryDTO> allInventories = inventoryService.findAllInventoryDTOs();

        Map<String, List<InventoryDTO>> inventoryMap = new HashMap<>();

        for (InventoryDTO inv : allInventories) {
            String matId = inv.getMatId();
            inventoryMap.computeIfAbsent(matId, k -> new ArrayList<>()).add(inv);
        }

        model.addAttribute("inventoryMap", inventoryMap);

        return "page/warehouse/outgoing/status";
    }

    // 출고 이력
    @GetMapping("/history")
    public String listOutgoing(PageRequestDTO pageRequestDTO, Model model) {

        // 전체 납입지시 상세 항목 페이징 조회
        PageResponseDTO<OutgoingDTO> outgoingList =
                outgoingService.listWithOutgoing(pageRequestDTO);

        model.addAttribute("outgoingList", outgoingList.getDtoList());
        model.addAttribute("pageRequestDTO", pageRequestDTO);
        model.addAttribute("totalCount", outgoingList.getTotal());

        return "page/warehouse/outgoing/history";
    }

    // 출고 처리 결과 조회
    @GetMapping("/result")
    public String listOutgoingResult(PageRequestDTO pageRequestDTO, Model model) {

        pageRequestDTO.setOutgoingStatus("출고마감");

        // 전체 납입지시 상세 항목 페이징 조회
        PageResponseDTO<OutgoingTotalDTO> allResults =
                outgoingTotalService.listWithOutgoingTotal(pageRequestDTO);

//        List<OutgoingTotalDTO> filteredList = allResults.getDtoList().stream()
//                .filter(dto -> "출고마감".equals(dto.getOutgoingStatus()))
//                .toList();
//
//        model.addAttribute("outgoingTotalList", filteredList);
//        model.addAttribute("pageRequestDTO", pageRequestDTO);
//        model.addAttribute("totalCount", filteredList.size());

        model.addAttribute("outgoingTotalList", allResults.getDtoList());
        model.addAttribute("pageRequestDTO", pageRequestDTO);
        model.addAttribute("totalCount", allResults.getTotal());

        return "page/warehouse/outgoing/result";
    }
}
