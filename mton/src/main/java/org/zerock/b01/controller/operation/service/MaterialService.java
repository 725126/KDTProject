package org.zerock.b01.controller.operation.service;

import org.zerock.b01.domain.operation.StatusTuple;
import org.zerock.b01.dto.operation.MaterialDTO;

import java.util.ArrayList;
import java.util.List;

public interface MaterialService {
    StatusTuple registerAll(List<MaterialDTO> list);
    List<MaterialDTO> viewAll();
    StatusTuple deleteAll(ArrayList<String> arrayList);
    StatusTuple updateAll(List<MaterialDTO> list);
}
