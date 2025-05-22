package org.zerock.b01.repository.warehouse;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.zerock.b01.domain.operation.Transaction;
import org.zerock.b01.domain.operation.TransactionItem;
import org.zerock.b01.dto.warehouse.TransactionViewDTO;
import org.zerock.b01.repository.search.warehouse.TransactionItemSearch;

import java.util.List;

public interface TransactionItemRepository extends JpaRepository<TransactionItem, Long>{
    List<TransactionItem> findByTransaction(Transaction transaction);

    // 조건 없이 전체 발행 대상만 조회
    @Query("""
    SELECT new org.zerock.b01.dto.warehouse.TransactionViewDTO(
        o.orderId,
        p.pCompany,
        p.partnerId,
        m.matId,
        m.matName,
        c.cmtPrice,
        SUM(i.incomingEffectiveQty),
        SUM(i.incomingEffectiveQty * c.cmtPrice)
    )
    FROM IncomingTotal i
    JOIN i.deliveryRequestItem dri
    JOIN dri.deliveryRequest dr
    JOIN dr.ordering o
    JOIN o.contractMaterial c
    JOIN c.material m
    JOIN c.contract.partner p
    WHERE o.transIssued = false AND o.orderStat = '완료'
    GROUP BY o.orderId, p.pCompany, p.partnerId, m.matId, m.matName, c.cmtPrice
    """)
    List<TransactionViewDTO> findAllTransactionItemsForIssue();

}
