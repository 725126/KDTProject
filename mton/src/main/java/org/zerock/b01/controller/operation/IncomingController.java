package org.zerock.b01.controller.operation;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.zerock.b01.dto.PageRequestDTO;
import org.zerock.b01.dto.PageResponseDTO;
import org.zerock.b01.dto.warehouse.IncomingDTO;
import org.zerock.b01.dto.warehouse.TransactionViewDTO;
import org.zerock.b01.service.warehouse.TransactionItemService;

@Controller("incomingOperationController")
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/internal/incoming")
public class IncomingController { // 입고

    private final TransactionItemService transactionItemService;

    // 거래 명세
    @GetMapping("/trans")
    public String listIncomingTransaction(PageRequestDTO pageRequestDTO, Model model) {

        // 전체 납입지시 상세 항목 페이징 조회
        PageResponseDTO<TransactionViewDTO> TransationList =
                transactionItemService.listWithTransactionItem(pageRequestDTO);

        model.addAttribute("TransationList", TransationList.getDtoList());
        model.addAttribute("pageRequestDTO", pageRequestDTO);
        model.addAttribute("totalCount", TransationList.getTotal());

        return "page/operation/incoming/trans";
    }
}
