package org.zerock.b01.controller.operation.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.zerock.b01.controller.operation.repository.*;
import org.zerock.b01.domain.operation.Pbom;
import org.zerock.b01.domain.operation.ProcurementPlan;
import org.zerock.b01.domain.operation.StatusTuple;
import org.zerock.b01.dto.operation.ProcurementPlanDTO;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
@Transactional
public class ProcurementPlanServiceImpl implements ProcurementPlanService {
    private final ProcurementPlanRepository procurementPlanRepository;
    private final MaterialRepository materialRepository;
    private final ProductionPlanRepository productionPlanRepository;
    private final OrderingRepository orderingRepository;
    private final PbomRepository pbomRepository;

    @Override
    public StatusTuple registerAll(List<ProcurementPlanDTO> list) {
        try {

            var pplans = list.stream().map(pplan -> ProcurementPlan.builder()
                    .pplanId(pplan.getPplanId())
                    .prdplan(productionPlanRepository.findById(pplan.getPrdplanId()).get())
                    .material(materialRepository.findById(pplan.getMaterialId()).get())
                    .ppmatQty(pplan.getPpmatQty())
                    .pplanDate(LocalDate.now())
                    .pplanEnd(pplan.getPplanEnd())
                    .pplanStat("진행중")
                    .build()
            ).collect(Collectors.toList());

            boolean hasDuple = false;
            boolean hasTwin = false;
            HashSet<String> twins = new HashSet<>();
            StringBuilder dupleMessage = new StringBuilder("다음 PrdPlan코드가 이미 존재합니다: ");
            StringBuilder twinMessage = new StringBuilder("다음 PrdPlan는 두 번 이상 입력되었습니다: ");

            for (var plan : pplans) {
                String id = plan.getPplanId();
                log.info("넘어온값: " + id);

                if (procurementPlanRepository.existsById(id)) {
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

            log.info(pplans);
            procurementPlanRepository.saveAll(pplans);
            return new StatusTuple(true, "모든 PPLAN 을 등록하였습니다.");
        } catch (Exception e) {
            return new StatusTuple(false, e.getMessage());
        }
    }

    @Override
    public List<ProcurementPlanDTO> viewAll() {
        return procurementPlanRepository.findAll().stream().map(plan -> {
            String status;
            LocalDate end = plan.getPplanEnd();
            LocalDate now = LocalDate.now();

            if (!plan.getPplanStat().equals("완료")) {
                if (end.isBefore(now)) { status = "기한초과"; }
                else if (end.isEqual(now)) { status = "만기일"; }
                else { status = "진행중"; }
            } else {
                status = plan.getPplanStat();
            }

            return ProcurementPlanDTO.builder()
                    .pplanId(plan.getPplanId())
                    .prdplanId(plan.getPrdplan().getPrdplanId())
                    .materialId(plan.getMaterial().getMatId())
                    .materialName(plan.getMaterial().getMatName())
                    .ppmatQty(plan.getPpmatQty())
                    .pplanDate(plan.getPplanDate())
                    .pplanEnd(plan.getPplanEnd())
                    .pplanStat(status)
                    .build();
        }).collect(Collectors.toList());
    }

    @Override
    public StatusTuple deleteAll(ArrayList<String> arrayList) {
        try {
            procurementPlanRepository.deleteAllById(arrayList);
            return new StatusTuple(true, "PRDPLAN 수정 성공했습니다.");
        } catch (Exception e) {
            return new StatusTuple(false, e.getMessage());
        }
    }

    @Override
    public StatusTuple updateAll(List<ProcurementPlanDTO> list) {
        try {
            var pplans = list.stream().map(pplan -> ProcurementPlan.builder()
                    .pplanId(pplan.getPplanId())
                    .prdplan(productionPlanRepository.findById(pplan.getPrdplanId()).get())
                    .material(materialRepository.findById(pplan.getMaterialId()).get())
                    .ppmatQty(pplan.getPpmatQty())
                    .pplanEnd(pplan.getPplanEnd())
                    .pplanStat(pplan.getPplanStat())
                    .build()).collect(Collectors.toList());

            var pplanNames = list.stream().map(ProcurementPlanDTO::getPplanId).collect(Collectors.toList());

            if (pplans.size() != procurementPlanRepository.findAllById(pplanNames).size()) {
                return new StatusTuple(false, "PPLAN 수정사항 개수가 일치하지 않습니다.");
            }

            procurementPlanRepository.saveAll(pplans);
            return new StatusTuple(true, "모든 PPLAN 수정사항을 반영했습니다.");
        } catch (Exception e) {
            return new StatusTuple(false, e.getMessage());
        }
    }
}
