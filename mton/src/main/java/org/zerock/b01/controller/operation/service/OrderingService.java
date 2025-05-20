package org.zerock.b01.controller.operation.service;

import org.zerock.b01.domain.operation.StatusTuple;
import org.zerock.b01.dto.operation.OrderingDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public interface OrderingService {
    StatusTuple registerAll(List<OrderingDTO> list);
    List<OrderingDTO> viewAll();
    StatusTuple deleteAll(ArrayList<String> arrayList);
    StatusTuple updateAll(List<OrderingDTO> list);
    StatusTuple updateStat(HashMap<String, String> hashMap);
}
