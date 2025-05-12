package org.zerock.b01.controller.operation.service;

import org.zerock.b01.domain.operation.StatusTuple;
import org.zerock.b01.dto.operation.ProductDTO;

import java.util.List;

public interface ProductService {
    StatusTuple registerAll(List<ProductDTO> list);
    List<ProductDTO> viewAll();
    StatusTuple deleteAll (List<String> arrayList);
    StatusTuple updateAll(List<ProductDTO> list);
}
