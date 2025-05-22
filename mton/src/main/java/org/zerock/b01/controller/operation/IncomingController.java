package org.zerock.b01.controller.operation;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.zerock.b01.domain.operation.Transaction;
import org.zerock.b01.dto.PageRequestDTO;
import org.zerock.b01.dto.PageResponseDTO;
import org.zerock.b01.dto.warehouse.IncomingDTO;
import org.zerock.b01.dto.warehouse.TransactionListDTO;
import org.zerock.b01.dto.warehouse.TransactionViewDTO;
import org.zerock.b01.security.CustomUserDetails;
import org.zerock.b01.service.warehouse.TransactionItemService;

import java.util.List;

@Controller("incomingOperationController")
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/internal/incoming")
public class IncomingController { // 입고

    private final TransactionItemService transactionItemService;

    // 거래 명세 발행
    @GetMapping("/trans")
    public String listIncomingTransaction(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) List<String> category,
            @RequestParam(defaultValue = "newest") String sort,
            Model model) {

        // 전체 거래 명세 발행 대상 상세 항목 조회
        List<TransactionViewDTO> TransationList = transactionItemService.listWithTransactionItem(keyword, category, sort);

        log.info(TransationList);

        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedSort", sort);
        model.addAttribute("selectedCategory", category);
        model.addAttribute("TransationList", TransationList);

        return "page/operation/incoming/trans";
    }

    // [내부직원] 거래 명세 확인
    @GetMapping("/trans-list")
    public String transGet(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) List<String> category,
            @RequestParam(defaultValue = "newest") String sort,
            Model model) {

        Pageable pageable = switch (sort) {
            case "oldest" -> PageRequest.of(page, size, Sort.by("tranDate").ascending());
            case "tranIdAsc" -> PageRequest.of(page, size, Sort.by("tranId").ascending());
            case "totalAmountDes" -> PageRequest.of(page, size, Sort.by("totalAmount").descending());
            default -> PageRequest.of(page, size, Sort.by("tranDate").descending());
        };

        Page<TransactionListDTO> transactionPage = transactionItemService.getAllIssuedTransactions(
                keyword, category, pageable);

        log.info("[내부직원] 거래 명세 확인: {}", transactionPage);

        model.addAttribute("transactionList", transactionPage.getContent());
        model.addAttribute("currentPage", page + 1);
        model.addAttribute("totalPages", transactionPage.getTotalPages());
        model.addAttribute("selectedSort", sort);
        model.addAttribute("selectedSize", size);
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedCategory", category);

        return "page/operation/incoming/trans-list";
    }
}
