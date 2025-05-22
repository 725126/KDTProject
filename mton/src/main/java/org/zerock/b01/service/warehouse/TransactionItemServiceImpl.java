package org.zerock.b01.service.warehouse;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.zerock.b01.controller.operation.repository.MaterialRepository;
import org.zerock.b01.controller.operation.repository.OrderingRepository;
import org.zerock.b01.domain.operation.*;
import org.zerock.b01.domain.user.Partner;
import org.zerock.b01.domain.user.QPartner;
import org.zerock.b01.domain.warehouse.IncomingTotal;
import org.zerock.b01.dto.PageRequestDTO;
import org.zerock.b01.dto.PageResponseDTO;
import org.zerock.b01.dto.operation.PartnerTransactionDTO;
import org.zerock.b01.dto.operation.TransactionItemCreateDTO;
import org.zerock.b01.dto.warehouse.TransactionListDTO;
import org.zerock.b01.dto.warehouse.TransactionViewDTO;
import org.zerock.b01.repository.user.PartnerRepository;
import org.zerock.b01.repository.warehouse.IncomingTotalRepository;
import org.zerock.b01.repository.warehouse.TransactionItemRepository;
import org.zerock.b01.repository.warehouse.TransactionRepository;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.zerock.b01.domain.user.QPartner.partner;

@Service
@RequiredArgsConstructor
@Log4j2
public class TransactionItemServiceImpl implements TransactionItemService  {

    private final TransactionItemRepository transactionItemRepository;
    private final IncomingTotalRepository incomingTotalRepository;

    private final TransactionRepository transactionRepository;
    private final PartnerRepository partnerRepository;
    private final MaterialRepository materialRepository;
    private final OrderingRepository orderingRepository;

    private final JPAQueryFactory queryFactory;

    @Override
    public List<TransactionViewDTO> listWithTransactionItem(String keyword, List<String> category, String sort) {
        log.info("[TransactionItemServiceImpl] listWithTransactionItem - 필터 적용 발행 대상 자재 조회");

        List<TransactionViewDTO> result = transactionItemRepository.findAllTransactionItemsForIssue();

        // 날짜 필드 세팅
        result.forEach(item -> {
            LocalDateTime lastCompletedAtDateTime = incomingTotalRepository
                    .findLatestIncomingCompletedAtByOrderIdAndOrderStatCompleted(item.getOrderId());
            LocalDate lastCompletedAt = lastCompletedAtDateTime != null ? lastCompletedAtDateTime.toLocalDate() : null;
            item.setIncomingLastCompletedAt(lastCompletedAt);
        });

        // 필터링
        Stream<TransactionViewDTO> stream = result.stream();

        if (StringUtils.hasText(keyword) && category != null && !category.isEmpty()) {
            stream = stream.filter(dto -> {
                for (String cat : category) {
                    switch (cat) {
                        case "orderId":
                            if (dto.getOrderId() != null && dto.getOrderId().contains(keyword)) return true;
                            break;
                        case "pCompany":
                            if (dto.getPCompany() != null && dto.getPCompany().contains(keyword)) return true;
                            break;
                        case "matId":
                            if (dto.getMatId() != null && dto.getMatId().contains(keyword)) return true;
                            break;
                        case "matName":
                            if (dto.getMatName() != null && dto.getMatName().contains(keyword)) return true;
                            break;
                        case "titemQty":
                            if (String.valueOf(dto.getTitemQty()).contains(keyword)) return true;
                            break;
                        case "titemPrice":
                            if (String.valueOf(dto.getTitemPrice()).contains(keyword)) return true;
                            break;
                        case "amount":
                            if (String.valueOf(dto.getAmount()).contains(keyword)) return true;
                            break;
                        case "incomingLastCompletedAt":
                            if (dto.getIncomingLastCompletedAt() != null &&
                                    dto.getIncomingLastCompletedAt().toString().contains(keyword)) return true;
                            break;
                    }
                }
                return false;
            });
        }

        // 정렬
        Comparator<TransactionViewDTO> comparator = switch (sort) {
            case "oldest" -> Comparator.comparing(TransactionViewDTO::getIncomingLastCompletedAt);
            case "companyAsc" -> Comparator.comparing(TransactionViewDTO::getPCompany);
            case "materialAsc" -> Comparator.comparing(TransactionViewDTO::getMatName);
            default -> Comparator.comparing(TransactionViewDTO::getIncomingLastCompletedAt).reversed();
        };

        return stream.sorted(comparator).toList();
    }

//    public void createTransactionItemsForOrdering(Ordering ordering) {
//        List<IncomingTotal> incomingTotals = incomingTotalRepository.findByDeliveryRequestItem_DeliveryRequest_Ordering(ordering);
//
//        int totalQty = incomingTotals.stream()
//                .mapToInt(IncomingTotal::getIncomingEffectiveQty)
//                .sum();
//
//        // 가격 등은 계약자재(ContractMaterial) 기준이라 가정
//        int price = ordering.getContractMaterial().getCmtPrice();
//
//        TransactionItem item = TransactionItem.builder()
//                .ordering(ordering)
//                .material(ordering.getContractMaterial().getMaterial())
//                .titemPrice(price)
//                .titemQty(totalQty)
//                .amount(price * totalQty)
//                .remark("-")
//                .build();
//
//        transactionItemRepository.save(item);
//    }

    @Override
    @Transactional
    public void createTransactionByPartner(List<PartnerTransactionDTO> partnerTransactions) {
        for (PartnerTransactionDTO partnerGroup : partnerTransactions) {
            if (partnerGroup.getPartnerId() == null) {
                throw new IllegalArgumentException("❌ partnerId가 null입니다");
            }

            Partner partner = partnerRepository.findById(partnerGroup.getPartnerId())
                    .orElseThrow(() -> new IllegalArgumentException("해당 협력업체를 찾을 수 없습니다."));

            int totalAmount = partnerGroup.getItems().stream()
                    .mapToInt(item -> item.getQty() * item.getPrice())
                    .sum();

            String tranId = generateTransactionCode();
            Transaction tran = Transaction.builder()
                    .tranId(tranId)
                    .tranDate(LocalDate.now())
                    .partner(partner)
                    .totalAmount(totalAmount)
                    .build();

            transactionRepository.save(tran);

            for (TransactionItemCreateDTO item : partnerGroup.getItems()) {
                if (item.getMatId() == null || item.getOrderId() == null) {
                    throw new IllegalArgumentException("❌ 자재ID 또는 발주ID가 null입니다");
                }

                Material material = materialRepository.findById(item.getMatId())
                        .orElseThrow(() -> new IllegalArgumentException("자재 ID 없음: " + item.getMatId()));

                int qty = item.getQty();
                int price = item.getPrice();

                Ordering ordering = orderingRepository.findById(item.getOrderId())
                        .orElseThrow(() -> new IllegalArgumentException("해당 발주 정보 없음: " + item.getOrderId()));

                ordering.setTransIssued(true);

                TransactionItem tItem = TransactionItem.builder()
                        .titemStore("입고창고")
                        .titemQty(qty)
                        .titemPrice(price)
                        .amount(qty * price)
                        .material(material)
                        .ordering(ordering)
                        .transaction(tran)
                        .remark("자동생성") // remark 채우기
                        .build();

                transactionItemRepository.save(tItem);
            }
        }
    }

    public String generateTransactionCode() {
        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyMMdd"));
        String prefix = "TR" + datePart;

        // 오늘 날짜 기준 마지막 거래 수 조회 (예: TR240521-003 까지 있다면 다음은 004)
        Long countToday = transactionRepository.countByTranIdStartingWith(prefix);

        return String.format("%s-%03d", prefix, countToday + 1);
    }


    @Override
    public byte[] generateTransactionPdf(Transaction tran, List<TransactionItem> items) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document doc = new Document(PageSize.A4, 30, 30, 30, 30);
        PdfWriter.getInstance(doc, baos);
        doc.open();

        BaseFont bfKorean = BaseFont.createFont("C:\\Windows\\Fonts\\malgun.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        Font titleFont = new Font(bfKorean, 18, Font.BOLD);
        Font cellFont = new Font(bfKorean, 11);

        // 제목
        Paragraph title = new Paragraph("거래명세서", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        doc.add(title);
        doc.add(Chunk.NEWLINE);

        // 거래 정보 요약
        doc.add(new Paragraph("거래ID: " + tran.getTranId(), cellFont));
        doc.add(new Paragraph("거래일자: " + tran.getTranDate(), cellFont));
        doc.add(new Paragraph("협력업체: " + tran.getPartner().getPCompany(), cellFont));
        doc.add(Chunk.NEWLINE);

        // 테이블
        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);
        table.setWidths(new int[]{2, 4, 2, 2, 2, 3});

        Stream.of("발주코드", "자재명", "수량", "단가", "금액", "비고").forEach(col -> {
            PdfPCell cell = new PdfPCell(new Phrase(col, cellFont));
            cell.setBackgroundColor(new BaseColor(240, 240, 240));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
        });

        for (TransactionItem item : items) {
            table.addCell(new Phrase(item.getOrdering().getOrderId(), cellFont));
            table.addCell(new Phrase(item.getMaterial().getMatName(), cellFont));
            table.addCell(new Phrase(String.valueOf(item.getTitemQty()), cellFont));
            table.addCell(new Phrase(String.valueOf(item.getTitemPrice()), cellFont));
            table.addCell(new Phrase(String.valueOf(item.getAmount()), cellFont));
            table.addCell(new Phrase(item.getRemark(), cellFont));
        }

        doc.add(table);
        doc.close();

        return baos.toByteArray();
    }

    @Override
    public Page<Transaction> searchPartnerTransactions(Partner partner, String keyword, List<String> category, Pageable pageable) {
        QTransaction transaction = QTransaction.transaction;
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(transaction.partner.eq(partner));

        if (StringUtils.hasText(keyword) && category != null && !category.isEmpty()) {
            BooleanBuilder keywordBuilder = new BooleanBuilder();
            for (String cat : category) {
                switch (cat) {
                    case "tranId" -> keywordBuilder.or(transaction.tranId.containsIgnoreCase(keyword));
                    case "tranDate" -> keywordBuilder.or(transaction.tranDate.stringValue().contains(keyword));
                    case "totalAmount" -> {
                        if (NumberUtils.isCreatable(keyword)) {
                            keywordBuilder.or(transaction.totalAmount.eq(Integer.parseInt(keyword)));
                        }
                    }
                }
            }
            builder.and(keywordBuilder);
        }

        return transactionRepository.findAll(builder, pageable);
    }

    @Override
    public Page<Transaction> searchAllTransactions(String keyword, List<String> category, Pageable pageable) {
        QTransaction transaction = QTransaction.transaction;
        BooleanBuilder builder = new BooleanBuilder(); // 조건 없음

        if (StringUtils.hasText(keyword) && category != null && !category.isEmpty()) {
            BooleanBuilder keywordBuilder = new BooleanBuilder();
            for (String cat : category) {
                switch (cat) {
                    case "tranId" -> keywordBuilder.or(transaction.tranId.containsIgnoreCase(keyword));
                    case "tranDate" -> keywordBuilder.or(transaction.tranDate.stringValue().contains(keyword));
                    case "totalAmount" -> {
                        if (NumberUtils.isCreatable(keyword)) {
                            keywordBuilder.or(transaction.totalAmount.eq(Integer.parseInt(keyword)));
                        }
                    }
                }
            }
            builder.and(keywordBuilder);
        }

        return transactionRepository.findAll(builder, pageable);
    }

    @Override
    public Page<TransactionListDTO> getAllIssuedTransactions(String keyword, List<String> category, Pageable pageable) {
        QTransaction transaction = QTransaction.transaction;
        QPartner partner = QPartner.partner;

        JPAQuery<Transaction> query = queryFactory
                .selectFrom(transaction)
                .join(transaction.partner, partner);

        BooleanBuilder builder = new BooleanBuilder();

        if (StringUtils.hasText(keyword) && category != null && !category.isEmpty()) {
            BooleanBuilder keywordBuilder = new BooleanBuilder();
            for (String cat : category) {
                switch (cat) {
                    case "tranId" -> keywordBuilder.or(transaction.tranId.containsIgnoreCase(keyword));
                    case "tranDate" -> keywordBuilder.or(transaction.tranDate.stringValue().contains(keyword));
                    case "totalAmount" -> {
                        if (NumberUtils.isCreatable(keyword)) {
                            keywordBuilder.or(transaction.totalAmount.eq(Integer.parseInt(keyword)));
                        }
                    }
                    case "companyName" -> keywordBuilder.or(partner.pCompany.containsIgnoreCase(keyword));
                }
            }
            builder.and(keywordBuilder);
        }

        query.where(builder);
        query.offset(pageable.getOffset()).limit(pageable.getPageSize());

        List<Transaction> results = query.fetch();
        long total = query.fetchCount();

        List<TransactionListDTO> dtoList = results.stream().map(tran ->
                TransactionListDTO.builder()
                        .tranId(tran.getTranId())
                        .tranDate(tran.getTranDate())
                        .totalAmount(tran.getTotalAmount())
                        .companyName(tran.getPartner().getPCompany())
                        .build()
        ).toList();

        return new PageImpl<>(dtoList, pageable, total);
    }


}