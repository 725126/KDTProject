package org.zerock.b01.repository.operation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.b01.domain.operation.TransactionItem;

import java.util.List;

public interface TransactionItemRepository extends JpaRepository<TransactionItem, Long> {
    // 특정 거래명세서에 속한 항목들 조회
    List<TransactionItem> findByTransaction_TranId(String tranId);
}
