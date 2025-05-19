package org.zerock.b01.controller.operation.service;

import org.zerock.b01.domain.operation.ContractMaterial;
import org.zerock.b01.domain.operation.StatusTuple;
import org.zerock.b01.dto.operation.OrderingDTO;

import java.util.List;

public interface InspectionService {
    void registerByCmt(List<OrderingDTO> list);
}
