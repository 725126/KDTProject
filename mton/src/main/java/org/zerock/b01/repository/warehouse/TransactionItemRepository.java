package org.zerock.b01.repository.warehouse;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.b01.domain.operation.TransactionItem;
import org.zerock.b01.repository.search.warehouse.TransactionItemSearch;

public interface TransactionItemRepository extends JpaRepository<TransactionItem, Long>, TransactionItemSearch {
}
