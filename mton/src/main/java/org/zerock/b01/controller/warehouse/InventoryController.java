package org.zerock.b01.controller.warehouse;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.zerock.b01.dto.PageRequestDTO;
import org.zerock.b01.dto.PageResponseDTO;
import org.zerock.b01.dto.warehouse.CompanyStorageDTO;
import org.zerock.b01.dto.warehouse.InventoryHistoryDTO;
import org.zerock.b01.dto.warehouse.InventoryTotalDTO;
import org.zerock.b01.service.warehouse.InventoryHistoryService;
import org.zerock.b01.service.warehouse.InventoryTotalService;

@Controller
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/internal/inventory")
public class InventoryController {

    private final InventoryTotalService inventoryTotalService;
    private final InventoryHistoryService inventoryHistoryService;

    // 재고 조회
    @GetMapping("/status")
    public String listInventoryStatus(PageRequestDTO pageRequestDTO, Model model) {

        // 전체 납입지시 상세 항목 페이징 조회
        PageResponseDTO<InventoryTotalDTO> inventoryList =
                inventoryTotalService.listWithInventoryTotal(pageRequestDTO);

        model.addAttribute("inventoryList", inventoryList.getDtoList());
        model.addAttribute("pageRequestDTO", pageRequestDTO);

        model.addAttribute("currentPage", inventoryList.getPage() + 1);
        model.addAttribute("totalPages", inventoryList.getEnd());
        model.addAttribute("selectedSize", pageRequestDTO.getSize());

        return "page/warehouse/inventory/status";
    }

    // 재고 이력
    @GetMapping("/history")
    public String listInventoryHistory(PageRequestDTO pageRequestDTO, Model model) {

        // 전체 납입지시 상세 항목 페이징 조회
        PageResponseDTO<InventoryHistoryDTO> inventoryHistoryList =
                inventoryHistoryService.listWithInventoryHistory(pageRequestDTO);

        model.addAttribute("inventoryHistoryList", inventoryHistoryList.getDtoList());
        model.addAttribute("pageRequestDTO", pageRequestDTO);

        model.addAttribute("currentPage", inventoryHistoryList.getPage() + 1);
        model.addAttribute("totalPages", inventoryHistoryList.getEnd());
        model.addAttribute("selectedSize", pageRequestDTO.getSize());

        return "page/warehouse/inventory/history";
    }

    // 재고 금액
    @GetMapping("/price")
    public String listInventoryPrice(PageRequestDTO pageRequestDTO, Model model) {

        // 전체 납입지시 상세 항목 페이징 조회
        PageResponseDTO<InventoryTotalDTO> inventoryList =
                inventoryTotalService.listWithInventoryTotal(pageRequestDTO);

        model.addAttribute("inventoryList", inventoryList.getDtoList());
        model.addAttribute("pageRequestDTO", pageRequestDTO);

        model.addAttribute("currentPage", inventoryList.getPage() + 1);
        model.addAttribute("totalPages", inventoryList.getEnd());
        model.addAttribute("selectedSize", pageRequestDTO.getSize());

        return "page/warehouse/inventory/price";
    }

    @GetMapping("/companyStorage")
    public String listInventoryCompanyStorage(Model model) {
        model.addAttribute("companyStorageDTO", new CompanyStorageDTO());
        return "page/warehouse/inventory/companyStorage";
    }
}
