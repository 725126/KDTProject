package org.zerock.b01.controller.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.zerock.b01.domain.operation.Transaction;
import org.zerock.b01.domain.operation.TransactionItem;

import org.zerock.b01.dto.operation.TransactionRequestWrapperDTO;
import org.zerock.b01.repository.warehouse.TransactionItemRepository;
import org.zerock.b01.repository.warehouse.TransactionRepository;
import org.zerock.b01.security.CustomUserDetails;
import org.zerock.b01.service.warehouse.TransactionItemService;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Log4j2
@RequiredArgsConstructor
@RestController
public class TransactionController {

    private final TransactionItemService transactionItemService;

    private final TransactionRepository transactionRepository;
    private final TransactionItemRepository transactionItemRepository;

    // 거래 명세서 생성
    @PostMapping("/internal/transaction/statement")
    public ResponseEntity<?> generateStatements(@RequestBody TransactionRequestWrapperDTO requestWrapper) {
        try {
            log.info("Generating statements for {}", requestWrapper);
            transactionItemService.createTransactionByPartner(requestWrapper.getPartnerTransactions());
            return ResponseEntity.ok().body(Map.of("status", "success"));
        } catch (Exception e) {
            log.error("거래명세서 생성 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("status", "error", "message", e.getMessage()));
        }
    }

    // 거래 명세 파일 확인
    @GetMapping("/trans/pdf/{tranId}")
    public ResponseEntity<byte[]> downloadTransactionPdf(@PathVariable String tranId) throws Exception {
        Transaction tran = transactionRepository.findById(tranId)
                .orElseThrow(() -> new IllegalArgumentException("거래명세서를 찾을 수 없습니다."));
        List<TransactionItem> items = transactionItemRepository.findByTransaction(tran);

        byte[] pdf = transactionItemService.generateTransactionPdf(tran, items);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.inline()
                .filename("거래명세서_" + tran.getTranId() + ".pdf", StandardCharsets.UTF_8)
                .build());

        return ResponseEntity.ok().headers(headers).body(pdf);
    }
}
