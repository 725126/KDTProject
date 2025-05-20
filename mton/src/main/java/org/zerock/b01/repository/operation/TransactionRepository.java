package org.zerock.b01.repository.operation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.b01.domain.operation.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    // 거래명세서 ID로 단건 조회 (기본 제공됨)

    // 협력업체별 거래명세서 목록 조회 등 추가로 필요 시 여기에 @Query 작성 가능
}
