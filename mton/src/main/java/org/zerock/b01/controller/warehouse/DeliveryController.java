package org.zerock.b01.controller.warehouse;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.zerock.b01.domain.warehouse.CompanyStorage;
import org.zerock.b01.dto.PageRequestDTO;
import org.zerock.b01.dto.PageResponseDTO;
import org.zerock.b01.dto.warehouse.DeliveryRequestDTO;
import org.zerock.b01.dto.warehouse.DeliveryRequestItemDTO;
import org.zerock.b01.service.warehouse.DeliveryRequestItemService;
import org.zerock.b01.service.warehouse.DeliveryRequestService;

import java.util.List;

@Controller
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/internal/delivery")
public class DeliveryController {

    private final DeliveryRequestService deliveryRequestService;
    private final DeliveryRequestItemService deliveryRequestItemService;

    //납입지시요청페이지
    @GetMapping("/request")
    public String listDeliveryRequests(PageRequestDTO pageRequestDTO,
                                       Model model) {

        PageResponseDTO<DeliveryRequestDTO> responseDTO = deliveryRequestService.listWithDeliveryRequest(pageRequestDTO);

        log.info(responseDTO);

        model.addAttribute("responseDTO", responseDTO);
        model.addAttribute("pageRequestDTO", pageRequestDTO);

        model.addAttribute("currentPage", responseDTO.getPage() + 1);
        model.addAttribute("totalPages", responseDTO.getEnd());
        model.addAttribute("selectedSize", pageRequestDTO.getSize());

        return "page/warehouse/delivery/request";
    }

    //납입지시등록페이지
    @GetMapping("/instruction")
    public String readDeliveryRequest(String orderId, PageRequestDTO pageRequestDTO, Model model) {

        DeliveryRequestDTO deliveryRequestDTO = deliveryRequestService.readDeliveryRequestOne(orderId);
        List<CompanyStorage> companyStorageList = deliveryRequestService.getCompanyStorageList();

        log.info(deliveryRequestDTO);

        model.addAttribute("dto", deliveryRequestDTO);
        model.addAttribute("companyStorageList", companyStorageList);

        Long drId = deliveryRequestDTO.getDrId(); // drId는 DeliveryRequestDTO 안에 있어야 함
        PageResponseDTO<DeliveryRequestItemDTO> drItemList =
                deliveryRequestItemService.getListDeliveryRequestItem(drId, pageRequestDTO);

        model.addAttribute("drItemList", drItemList.getDtoList());
        model.addAttribute("pageRequestDTO", pageRequestDTO);
        model.addAttribute("totalPages", drItemList.getTotal());

        return "page/warehouse/delivery/instruction";

    }

    //납입지시조회페이지
    @GetMapping("/status")
    public String listDeliveryRequestItems(PageRequestDTO pageRequestDTO, Model model) {

        // 전체 납입지시 상세 항목 페이징 조회
        PageResponseDTO<DeliveryRequestItemDTO> drItemList =
                deliveryRequestItemService.listWithDeliveryRequestItem(pageRequestDTO);

        model.addAttribute("drItemList", drItemList.getDtoList());
        model.addAttribute("pageRequestDTO", pageRequestDTO);

        model.addAttribute("currentPage", drItemList.getPage() + 1);
        model.addAttribute("totalPages", drItemList.getEnd());
        model.addAttribute("selectedSize", pageRequestDTO.getSize());

        return "page/warehouse/delivery/status";
    }

}
