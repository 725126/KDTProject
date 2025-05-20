package org.zerock.b01.service.warehouse;

import org.zerock.b01.domain.operation.ProductionPlan;
import org.zerock.b01.domain.operation.StatusTuple;
import org.zerock.b01.dto.PageRequestDTO;
import org.zerock.b01.dto.PageResponseDTO;
import org.zerock.b01.dto.warehouse.OutgoingTotalDTO;

import java.util.List;

public interface OutgoingTotalService {

  void createOutgoingTotalListByPrdPlan(ProductionPlan plan);

  StatusTuple validateDeletePlans(List<String> prdplanIds);

  PageResponseDTO<OutgoingTotalDTO> listWithOutgoingTotal(PageRequestDTO pageRequestDTO);
}
