package org.zerock.b01.service.operation;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.b01.controller.operation.repository.ContractMaterialRepository;
import org.zerock.b01.domain.operation.*;
import org.zerock.b01.domain.user.Partner;
import org.zerock.b01.domain.warehouse.CompanyStorage;
import org.zerock.b01.domain.warehouse.CompanyStorageItem;
import org.zerock.b01.domain.warehouse.IncomingItem;
import org.zerock.b01.domain.warehouse.IncomingTotal;
import org.zerock.b01.repository.operation.TransactionItemRepository;
import org.zerock.b01.repository.operation.TransactionRepository;
import org.zerock.b01.repository.warehouse.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Log4j2
public class TransactionServiceImpl implements TransactionService {

    private final IncomingItemRepository incomingItemRepository;
    private final IncomingTotalRepository incomingTotalRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionItemRepository transactionItemRepository;
    private final ContractMaterialRepository contractMaterialRepository;
    private final CompanyStorageItemRepository companyStorageItemRepository;


    @Override
    @Transactional
    public void generateFromIncoming(Long incomingTotalId) {
        IncomingTotal incomingTotal = incomingTotalRepository.findById(incomingTotalId)
                .orElseThrow(() -> new IllegalArgumentException("해당 입고 정보 없음"));

        if (!incomingTotal.getIncomingStatus().name().equals("입고마감")) {
            throw new IllegalStateException("입고마감 상태에서만 거래명세서를 발행할 수 있습니다.");
        }

        var drItem = incomingTotal.getDeliveryRequestItem();
        var ordering = drItem.getDeliveryRequest().getOrdering();
        ContractMaterial contractMaterial = ordering.getContractMaterial();
        Contract contract = contractMaterial.getContract();
        Partner partner = contract.getPartner();

        String tranId = "TR" + LocalDate.now() + "-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();

        Transaction transaction = Transaction.builder()
                .tranId(tranId)
                .tranDate(LocalDate.now())
                .partner(partner)
                .build();

        transactionRepository.save(transaction);

        // 입고된 자재 항목 기반으로 거래명세 항목 생성
        List<IncomingItem> incomingItems = incomingItemRepository
                .findByIncoming_IncomingTotal_IncomingTotalId(incomingTotalId);


        for (IncomingItem item : incomingItems) {
            CompanyStorageItem csi = companyStorageItemRepository.findById(item.getCstorageItem().getCstorageItemId())
                    .orElseThrow();

            Material material = csi.getMaterial();

            // 해당 자재의 계약정보 조회
            ContractMaterial cm = contractMaterialRepository.findByContractAndMaterial(contract, material)
                    .orElseThrow(() -> new IllegalArgumentException("계약 자재 정보를 찾을 수 없습니다."));

            TransactionItem titem = TransactionItem.builder()
                    .titemId(UUID.randomUUID().toString())
                    .transaction(transaction)
                    .material(material)
                    .titemQty(item.getIncomingQty())
                    .titemPrice(cm.getCmtPrice())
                    .titemStore(csi.getCompanyStorage().getCstorageId())
                    .build();

            transactionItemRepository.save(titem);
        }

        incomingTotal.markTransactionCreated();
        incomingTotalRepository.save(incomingTotal);

        log.info("✅ 거래명세서 발행 완료: {}", tranId);
    }
}
