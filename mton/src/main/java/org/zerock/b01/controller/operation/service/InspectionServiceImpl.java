package org.zerock.b01.controller.operation.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.zerock.b01.controller.operation.repository.InspectionRepository;
import org.zerock.b01.controller.operation.repository.OrderingRepository;
import org.zerock.b01.domain.operation.ContractMaterial;
import org.zerock.b01.domain.operation.Inspection;
import org.zerock.b01.dto.operation.OrderingDTO;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
@Transactional
public class InspectionServiceImpl implements InspectionService {
    private final InspectionRepository inspectionRepository;
    private final OrderingRepository orderingRepository;

    // TODO: 작업중임
    @Override
    public void registerByCmt(List<OrderingDTO> list) {
        AtomicInteger atomicInteger = new AtomicInteger(1);

        List<Inspection> inspectionList = list.stream().map(ord -> {
            String id = "";
            String count = inspectionRepository.findLastOrderIdByPrefix("IS");
            if (count != null) {
                int countId = Integer.parseInt(count.substring(count.indexOf("IS") + 2)) + atomicInteger.getAndIncrement();
                id = "IS" + String.format("%3d", countId).replace(" ", "0");
            } else {
                int countId = atomicInteger.getAndIncrement();
                id = "IS" + String.format("%3d", countId).replace(" ", "0");
            }

            return Inspection.builder()
                    .insId(id)
                    .ordering(orderingRepository.findById(ord.getOrderId()).get())
                    .insStart(ord.getOrderDate())
                    .insEnd(ord.getOrderEnd())
                    .insQty(0)
                    .insTotal(ord.getOrderQty())
                    .insStat("진행중")
                    .build();
        }).collect(Collectors.toList());

        inspectionRepository.saveAll(inspectionList);
    }
}
