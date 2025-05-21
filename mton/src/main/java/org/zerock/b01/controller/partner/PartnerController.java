package org.zerock.b01.controller.partner;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.zerock.b01.controller.operation.repository.InspectionRepository;
import org.zerock.b01.controller.operation.repository.OrderingRepository;
import org.zerock.b01.controller.operation.service.ContractMaterialService;
import org.zerock.b01.controller.operation.service.InspectionService;
import org.zerock.b01.controller.operation.service.OrderingService;
import org.zerock.b01.domain.operation.Inspection;
import org.zerock.b01.domain.operation.Ordering;
import org.zerock.b01.domain.operation.StatusTuple;
import org.zerock.b01.dto.PageRequestDTO;
import org.zerock.b01.dto.PageResponseDTO;
import org.zerock.b01.dto.partner.ContractMaterialDTO;
import org.zerock.b01.dto.warehouse.DeliveryPartnerDTO;
import org.zerock.b01.security.CustomUserDetails;
import org.zerock.b01.service.warehouse.DeliveryPartnerService;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/external")
public class PartnerController {
    private final ContractMaterialService contractMaterialService;
    private final OrderingRepository orderingRepository;
    private final InspectionRepository inspectionRepository;

    private final DeliveryPartnerService deliveryPartnerService;
    private final OrderingService orderingService;
    private final InspectionService inspectionService;

    // 계약 정보 열람
    @GetMapping("/contract/view")
    public String contractViewGet() {
        return "page/partner/contract-view";
    }

    // 계약 자재 등록
    @GetMapping("/contmat")
    public String contmatGet() {
        return "page/partner/contmat";
    }

    // 진척 검수 수행
    @GetMapping("/inspect")
    public String inspectGet(@AuthenticationPrincipal CustomUserDetails customUserDetails, Model model) {
        List<Ordering> orderings = orderingRepository.findOrderingByUserId(customUserDetails.getUserId());
        List<Inspection> inspections = inspectionRepository.findByOrderIds(orderings.stream().map(Ordering::getOrderId).collect(Collectors.toList())).stream().filter(x -> !x.getInsStat().equals("대기중")).collect(Collectors.toList());

        model.addAttribute("orders", orderings);
        model.addAttribute("inspections", inspections);
        return "page/partner/inspect";
    }

    @GetMapping("/inspect/alert")
    public String inspectAlertGet(@AuthenticationPrincipal CustomUserDetails customUserDetails, Model model) {
        List<Ordering> orderings = orderingRepository.findCancelOrderingByUserId(customUserDetails.getUserId());
        List<Inspection> inspections = inspectionRepository.findByOrderIds(orderings.stream().map(Ordering::getOrderId).collect(Collectors.toList()));

        model.addAttribute("orders", orderings);
        model.addAttribute("inspections", inspections);
        return "page/partner/inspectalert";
    }

    // 자재 재고 관리
    @GetMapping("/mat/inventory")
    public String matInventoryGet() {
        return "page/partner/mat-inventory";
    }

    // 거래 명세 활용
    @GetMapping("/trans")
    public String transGet() {
        return "page/partner/trans";
    }

    // 협력사 납품 지시 요청
    @GetMapping("/delivery")
    public String listDeliveryRequestItems(PageRequestDTO pageRequestDTO, Model model) {

        // 전체 납입지시 상세 항목 페이징 조회
        PageResponseDTO<DeliveryPartnerDTO> drPartnerList =
                deliveryPartnerService.listWithDeliveryPartner(pageRequestDTO);

        model.addAttribute("drPartnerList", drPartnerList.getDtoList());
        model.addAttribute("pageRequestDTO", pageRequestDTO);
        model.addAttribute("totalCount", drPartnerList.getTotal());

        return "page/partner/delivery";
    }

    @ResponseBody
    @PostMapping("/contmat/viewdata")
    public List<ContractMaterialDTO> viewContmat(@RequestBody String str) {
        log.info("view contmat: " + str);
        return contractMaterialService.viewAll();
    }

    @ResponseBody
    @PostMapping("/update/order")
    public StatusTuple updateOrderStat(@RequestBody List<HashMap<String, String>> list) {
        log.info("update order: " + list);
        HashMap<String, String> hashMap = new HashMap<>();
        for (HashMap<String, String> map : list) {
            hashMap.put(map.get("orderId"), map.get("orderStat"));
        }
        return orderingService.updateStat(hashMap);
    }

    @ResponseBody
    @PostMapping("/update/inspect")
    public StatusTuple updateInspect(@RequestBody List<HashMap<String, String>> list) {
        log.info("update inspect: " + list);
        HashMap<String, String> hashMap = new HashMap<>();
        for (HashMap<String, String> map : list) {
            hashMap.put(map.get("insId"), map.get("insQty"));
        }
        return inspectionService.updateQty(hashMap);
    }
}
