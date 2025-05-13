package org.zerock.b01.controller.operation.service;

import org.zerock.b01.domain.operation.StatusTuple;
import org.zerock.b01.dto.operation.ProcurementPlanDTO;

import java.util.ArrayList;
import java.util.List;

public interface ProcurementPlanService {
    StatusTuple registerAll(List<ProcurementPlanDTO> list);
    List<ProcurementPlanDTO> viewAll();
    StatusTuple deleteAll(ArrayList<String> arrayList);
    StatusTuple updateAll(List<ProcurementPlanDTO> list);
}
