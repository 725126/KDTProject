package org.zerock.b01.controller.operation.service;

import org.zerock.b01.domain.operation.StatusTuple;
import org.zerock.b01.dto.operation.PbomDTO;

import java.util.List;

public interface PbomService {
    StatusTuple registerAll(List<PbomDTO> list);
    List<PbomDTO> viewAll();
}
