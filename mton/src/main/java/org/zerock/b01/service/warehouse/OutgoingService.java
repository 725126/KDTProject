package org.zerock.b01.service.warehouse;

import org.zerock.b01.dto.PageRequestDTO;
import org.zerock.b01.dto.PageResponseDTO;
import org.zerock.b01.dto.warehouse.OutgoingDTO;

import java.util.List;

public interface OutgoingService {

  void partialOutgoing(OutgoingDTO dto);

  void fullOutgoing(List<OutgoingDTO> dtoList);

  PageResponseDTO<OutgoingDTO> listWithOutgoing(PageRequestDTO pageRequestDTO);
}
