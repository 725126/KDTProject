package org.zerock.b01.controller.operation.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.zerock.b01.controller.operation.repository.OrderingRepository;
import org.zerock.b01.controller.operation.repository.ProcurementPlanRepository;
import org.zerock.b01.controller.operation.repository.ProductRepository;
import org.zerock.b01.controller.operation.repository.ProductionPlanRepository;
import org.zerock.b01.domain.operation.Ordering;
import org.zerock.b01.domain.operation.ProcurementPlan;
import org.zerock.b01.domain.operation.ProductionPlan;
import org.zerock.b01.domain.operation.StatusTuple;
import org.zerock.b01.dto.operation.ProductionPlanDTO;
import org.zerock.b01.service.warehouse.IncomingTotalService;
import org.zerock.b01.service.warehouse.OutgoingTotalService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
@Transactional
public class ProductionPlanServiceImpl implements ProductionPlanService {
    private final ProductionPlanRepository productionPlanRepository;
    private final ProductRepository productRepository;
    private final OutgoingTotalService outgoingTotalService;
    private final ProcurementPlanRepository procurementPlanRepository;
    private final OrderingRepository orderingRepository;

    @Override
    public StatusTuple registerAll(List<ProductionPlanDTO> list) {
        try {
            var prdPlans = list.stream().map(prdplan -> ProductionPlan.builder()
                    .prdplanId(prdplan.getPrdplanId())
                    .prdplanDate(prdplan.getPrdplanDate())
                    .prdplanEnd(prdplan.getPrdplanEnd())
                    .prdplanQty(prdplan.getPrdplanQty())
                    .product(productRepository.findById(prdplan.getProdId()).get())
                    .build()
            ).collect(Collectors.toList());

            boolean hasDuple = false;
            boolean hasTwin = false;
            HashSet<String> twins = new HashSet<>();
            StringBuilder dupleMessage = new StringBuilder("다음 PrdPlan코드가 이미 존재합니다: ");
            StringBuilder twinMessage = new StringBuilder("다음 PrdPlan는 두 번 이상 입력되었습니다: ");

            for (var plan : prdPlans) {
                String id = plan.getPrdplanId();
                log.info("넘어온값: " + id);

                if (productionPlanRepository.existsById(id)) {
                    hasDuple = true;
                    dupleMessage.append(id).append(", ");
                }

                if (!twins.add(id)) {
                    hasTwin = true;
                    twinMessage.append(id).append(", ");
                }
            }

            if (hasDuple) {
                dupleMessage.append("PrdPlan 등록이 취소되었습니다.");
                return new StatusTuple(false, dupleMessage.toString());
            }

            if (hasTwin) {
                twinMessage.append("PrdPlan 등록이 취소되었습니다.");
                return new StatusTuple(false, twinMessage.toString());
            }

            log.info(prdPlans);
            productionPlanRepository.saveAll(prdPlans);

            for (ProductionPlan plan : prdPlans) {
                outgoingTotalService.createOutgoingTotalListByPrdPlan(plan);
            }

            return new StatusTuple(true, "모든 Prdplan이 등록되었습니다.");
        } catch (Exception e) {
            return new StatusTuple(false, e.getMessage());
        }
    }

    @Override
    public List<ProductionPlanDTO> viewAll() {
        return productionPlanRepository.findAll().stream().map(plan -> {
            String status;
            LocalDate end = plan.getPrdplanEnd();
            LocalDate now = LocalDate.now();

            if (end.isBefore(now) && plan.getPrdplanStat().equals("진행중")) { status = "기한초과"; }
            else { status = plan.getPrdplanStat(); }

            return ProductionPlanDTO.builder()
                    .prdplanId(plan.getPrdplanId())
                    .prodId(plan.getProduct().getProdId())
                    .prodName(plan.getProduct().getProdName())
                    .prdplanQty(plan.getPrdplanQty())
                    .prdplanDate(plan.getPrdplanDate())
                    .prdplanEnd(plan.getPrdplanEnd())
                    .prdplanStatus(status).build();
        }).collect(Collectors.toList());
    }

    @Override
    public StatusTuple deleteAll(ArrayList<String> arrayList) {
        try {
            productionPlanRepository.deleteAllById(arrayList);
            return new StatusTuple(true, "PRDPLAN 수정 성공했습니다.");
        } catch (Exception e) {
            return new StatusTuple(false, e.getMessage());
        }
    }

    @Override
    public StatusTuple updateAll(List<ProductionPlanDTO> list) {
        try {
            var prdpland = list.stream().map(prdplan -> ProductionPlan.builder()
                    .prdplanId(prdplan.getPrdplanId())
                    .product(productRepository.findById(prdplan.getProdId()).get())
                    .prdplanQty(prdplan.getPrdplanQty())
                    .prdplanEnd(prdplan.getPrdplanEnd())
                    .build()).collect(Collectors.toList());

            var prdplanNames = list.stream().map(ProductionPlanDTO::getPrdplanId).collect(Collectors.toList());

            if (prdpland.size() != productionPlanRepository.findAllById(prdplanNames).size()) {
                return new StatusTuple(false, "PRDPLAN 수정사항 개수가 일치하지 않습니다.");
            }

            productionPlanRepository.saveAll(prdpland);
            return new StatusTuple(true, "모든 PRDPLAN 수정사항을 반영하였습니다.");
        } catch (Exception e) {
            return new StatusTuple(false, e.getMessage());
        }
    }

    @Override
    public StatusTuple cancelAll(ArrayList<String> arrayList) {
        try {
            // 생산계획
            List<ProductionPlan> prdplans = productionPlanRepository.findAllById(arrayList);
            // 연관된 조달계획과 자재발주
            List<ProcurementPlan> pplans = procurementPlanRepository.findPPlanByPrdplanIds(arrayList).stream().filter(x -> !x.getPplanStat().equals("완료")).collect(Collectors.toList());
            List<Ordering> orderings = orderingRepository.findOrderingByPPlanId(pplans.stream().map(ProcurementPlan::getPplanId).collect(Collectors.toList())).stream().filter(x -> !x.getOrderStat().equals("완료")).collect(Collectors.toList());

            prdplans.forEach(x -> x.changeStat("취소"));
            pplans.forEach(x -> x.changeStat("취소"));
            orderings.forEach(x -> x.changeOrderStat("취소대기"));

            return new StatusTuple(true, "생산계획 취소.");
        } catch (Exception e) {
            return new StatusTuple(false, e.getMessage());
        }
    }
}
