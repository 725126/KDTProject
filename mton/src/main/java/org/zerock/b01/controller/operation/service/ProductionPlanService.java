package org.zerock.b01.controller.operation.service;

import org.zerock.b01.domain.operation.StatusTuple;
import org.zerock.b01.dto.operation.ProductionPlanDTO;

import java.util.ArrayList;
import java.util.List;

public interface ProductionPlanService {
    StatusTuple registerAll(List<ProductionPlanDTO> list);
    List<ProductionPlanDTO> viewAll();
    StatusTuple deleteAll(ArrayList<String> arrayList);
    StatusTuple updateAll(List<ProductionPlanDTO> list);
    StatusTuple cancelAll(ArrayList<String> arrayList);
}
