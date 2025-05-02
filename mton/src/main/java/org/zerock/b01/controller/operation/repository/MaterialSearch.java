package org.zerock.b01.controller.operation.repository;

import org.zerock.b01.dto.operation.MaterialDTO;

import java.util.List;

public interface MaterialSearch {
    List<MaterialDTO> search(String[] filters);
}
