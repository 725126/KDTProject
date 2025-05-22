package org.zerock.b01.service.warehouse;

import org.zerock.b01.domain.operation.ProductionPlan;
import org.zerock.b01.dto.PageRequestDTO;
import org.zerock.b01.dto.PageResponseDTO;
import org.zerock.b01.dto.warehouse.OutgoingTotalDTO;

public interface OutgoingTotalService {

  void createOutgoingTotalListByPrdPlan(ProductionPlan plan);

  PageResponseDTO<OutgoingTotalDTO> listWithOutgoingTotal(PageRequestDTO pageRequestDTO);

  void updateOutgoingStatus(Long outgoingTotalId);

  void closeOutgoing(Long outgoingTotalId);

}
