package org.zerock.b01.controller.operation.service;

import org.zerock.b01.domain.operation.StatusTuple;
import org.zerock.b01.dto.partner.ContractMaterialDTO;

import java.util.ArrayList;
import java.util.List;

public interface ContractMaterialService {
    StatusTuple registerAll(List<ContractMaterialDTO> list);
    List<ContractMaterialDTO> viewAll();
    StatusTuple deleteAll(ArrayList<String> arrayList);
    StatusTuple updateAll(List<ContractMaterialDTO> list);
}
