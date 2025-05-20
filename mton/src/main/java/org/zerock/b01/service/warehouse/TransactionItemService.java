package org.zerock.b01.service.warehouse;

import org.zerock.b01.domain.operation.Ordering;
import org.zerock.b01.dto.PageRequestDTO;
import org.zerock.b01.dto.PageResponseDTO;
import org.zerock.b01.dto.warehouse.TransactionViewDTO;

public interface TransactionItemService {

  PageResponseDTO<TransactionViewDTO> listWithTransactionItem(PageRequestDTO pageRequestDTO);

  void createTransactionItemsForOrdering(Ordering ordering);
}
