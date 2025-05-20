package org.zerock.b01.controller.operation.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.zerock.b01.controller.operation.repository.ContractMaterialRepository;
import org.zerock.b01.controller.operation.repository.OrderingRepository;
import org.zerock.b01.controller.operation.repository.ProcurementPlanRepository;
import org.zerock.b01.domain.operation.Ordering;
import org.zerock.b01.domain.operation.StatusTuple;
import org.zerock.b01.dto.operation.OrderingDTO;
import org.zerock.b01.service.warehouse.DeliveryRequestService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
@Transactional
public class OrderingServiceImpl implements OrderingService {
    private final ContractMaterialRepository contractMaterialRepository;
    private final ProcurementPlanRepository procurementPlanRepository;
    private final OrderingRepository orderingRepository;
    private final DeliveryRequestService deliveryRequestService;

    @Override
    public StatusTuple registerAll(List<OrderingDTO> list) {
        var orders = list.stream().map(ord -> Ordering.builder()
                .orderId(ord.getOrderId())
                .contractMaterial(contractMaterialRepository.findById(ord.getCmtId()).get())
                .procurementPlan(procurementPlanRepository.findById(ord.getPplanId()).get())
                .orderDate(ord.getOrderDate())
                .orderEnd(ord.getOrderEnd())
                .orderQty(ord.getOrderQty())
                .orderStat(ord.getOrderStat())
                .build()
        ).collect(Collectors.toList());

        boolean hasDuple = false;
        boolean hasTwin = false;
        HashSet<String> twins = new HashSet<>();
        StringBuilder dupleMessage = new StringBuilder("다음 자재코드가 이미 존재합니다: ");
        StringBuilder twinMessage = new StringBuilder("다음 자재코드는 두 번 이상 입력되었습니다: ");

        for (var ord : orders) {
            String id = ord.getOrderId();
            log.info("넘어온값: " + id);

            if (orderingRepository.existsById(id)) {
                hasDuple = true;
                dupleMessage.append(id).append(", ");
            }

            if (!twins.add(id)) {
                hasTwin = true;
                twinMessage.append(id).append(", ");
            }
        }

        if (hasDuple) {
            dupleMessage.append("자재 등록이 취소되었습니다.");
            return new StatusTuple(false, dupleMessage.toString());
        }

        if (hasTwin) {
            twinMessage.append("자재 등록이 취소되었습니다.");
            return new StatusTuple(false, twinMessage.toString());
        }

        orderingRepository.saveAll(orders);

        //납입지시 테이블 자동 등록 추가
        for (Ordering order : orders) {
            deliveryRequestService.createDeliveryRequestFromOrdering(order.getOrderId());
        }

        return new StatusTuple(true, "모든 발주를 등록했습니다.");
    }

    @Override
    public List<OrderingDTO> viewAll() {
        return orderingRepository.findAll().stream().map(ord ->
                OrderingDTO.builder()
                        .orderId(ord.getOrderId())
                        .cmtId(ord.getContractMaterial().getCmtId())
                        .pplanId(ord.getProcurementPlan().getPplanId())
                        .orderQty(ord.getOrderQty())
                        .orderDate(ord.getOrderDate())
                        .orderEnd(ord.getOrderEnd())
                        .orderStat(ord.getOrderStat())
                        .build()
        ).collect(Collectors.toList());
    }

    @Override
    public StatusTuple deleteAll(ArrayList<String> arrayList) {
        try {
            //제한사항 추가
            deliveryRequestService.deleteByOrderIds(arrayList);

            orderingRepository.deleteAllById(arrayList);
            return new StatusTuple(true, "발주 삭제에 성공했습니다.");
        } catch (Exception e) {
            return new StatusTuple(false, e.getMessage());
        }
    }

    @Override
    public StatusTuple updateAll(List<OrderingDTO> list) {
        try {

            //제한사항 추가
            deliveryRequestService.validateOrderingUpdate(list);

            var orders = list.stream().map(ord -> Ordering.builder()
                    .orderId(ord.getOrderId())
                    .contractMaterial(contractMaterialRepository.findById(ord.getCmtId()).get())
                    .procurementPlan(procurementPlanRepository.findById(ord.getPplanId()).get())
                    .orderDate(ord.getOrderDate())
                    .orderEnd(ord.getOrderEnd())
                    .orderQty(ord.getOrderQty())
                    .orderStat(ord.getOrderStat())
                    .build()
            ).collect(Collectors.toList());

            var orderNames = list.stream().map(OrderingDTO::getOrderId).collect(Collectors.toList());

            if (orders.size() != orderingRepository.findAllById(orderNames).size()) {
                return new StatusTuple(false, "발주 수정사항 개수가 일치하지 않습니다.");
            }

            orderingRepository.saveAll(orders);
            return new StatusTuple(true, "모든 발주 수정사항을 반영하였습니다.");
        } catch (Exception e) {
            return new StatusTuple(false, e.getMessage());
        }
    }

    @Override
    public StatusTuple updateStat(HashMap<String, String> hashMap) {
        try {
            List<Ordering> orderings = orderingRepository.findAllById(hashMap.keySet());

            orderings.forEach(ord -> {
                ord.changeOrderStat(hashMap.get(ord.getOrderId()));
            });

            orderingRepository.saveAll(orderings);
            return new StatusTuple(true, "상태 전환 성공");
        } catch (Exception e) {
            return new StatusTuple(false, e.getMessage());
        }
    }
}
