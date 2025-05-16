package org.zerock.b01.repository.search.warehouse;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.zerock.b01.domain.warehouse.IncomingItem;

import java.time.LocalDate;

public interface IncomingItemSearch {

  Page<IncomingItem> searchIncomingItem(LocalDate modifyDateStart,
                                        LocalDate modifyDateEnd, String incomingCode,
                                        String pCompany, String matId, String matName,
                                        String incomingItemStatus, Pageable pageable);
}
