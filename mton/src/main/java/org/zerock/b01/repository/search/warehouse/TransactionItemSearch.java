package org.zerock.b01.repository.search.warehouse;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.zerock.b01.domain.operation.TransactionItem;
import org.zerock.b01.dto.warehouse.TransactionViewDTO;

public interface TransactionItemSearch {

  Page<TransactionViewDTO> searchTransactionItem(String orderId, String pCompany, String matId,
                                                 String matName, Pageable pageable);
}
