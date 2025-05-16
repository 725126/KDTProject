package org.zerock.b01.service.warehouse;

import org.zerock.b01.dto.PageRequestDTO;
import org.zerock.b01.dto.PageResponseDTO;
import org.zerock.b01.dto.warehouse.IncomingItemDTO;

public interface IncomingItemService {

  PageResponseDTO<IncomingItemDTO> listWithIncomingItem(PageRequestDTO pageRequestDTO);

}
