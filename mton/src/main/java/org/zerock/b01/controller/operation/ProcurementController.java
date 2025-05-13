package org.zerock.b01.controller.operation;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.zerock.b01.controller.operation.repository.ProcurementPlanRepository;
import org.zerock.b01.controller.operation.service.ProcurementPlanService;
import org.zerock.b01.domain.operation.StatusTuple;
import org.zerock.b01.domain.operation.tablehead.ProcurementPlanTableHead;
import org.zerock.b01.dto.operation.ProcurementPlanDTO;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Controller
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/internal/procurement")
public class ProcurementController {
    private final ProcurementPlanRepository procurementPlanRepository;

    private final ProcurementPlanService procurementPlanService;

    //조달 계획
    @GetMapping("/procure")
    public String procureGet(Model model) {
        model.addAttribute("pplanTH", ProcurementPlanTableHead.values());
        return "/page/operation/procurement/procure";
    }

    // 계약 정보
    @GetMapping("/contract")
    public String contractGet() {
        return "/page/operation/procurement/contract";
    }

    // 자재 발주
    @GetMapping("/order")
    public String orderGet() {
        return "/page/operation/procurement/order";
    }

    // 진척 검수
    @GetMapping("/inspect")
    public String inspectGet() {
        return "/page/operation/procurement/inspect";
    }

    @ResponseBody
    @PostMapping("/register/pplan")
    public StatusTuple registerPplan(@RequestBody ArrayList<HashMap<String, String>> list) {
        log.info(list.toString());

        if (list.isEmpty()) {
            return new StatusTuple(false, "등록할 PPLAN 이 없습니다.");
        }

        AtomicInteger atomicInteger = new AtomicInteger(1);

        List<ProcurementPlanDTO> procurementPlanDTOList = list.stream().map(hashmap -> {
            String id = hashmap.get(ProcurementPlanTableHead.PPLAN_ID.getLabel());

            if (id == null || id.isEmpty()) {
                String count = procurementPlanRepository.findLastOrderIdByPrefix("PPLAN");

                if (count != null) {
                    int countId = Integer.parseInt(count.substring(count.indexOf("PPLAN") + 5)) + atomicInteger.getAndIncrement();
                    id = "PPLAN" + String.format("%03d", countId).replace(" ", "0");
                } else {
                    int countId = atomicInteger.getAndIncrement();
                    id = "PPLAN" + String.format("%03d", countId).replace(" ", "0");
                }
            }

            return ProcurementPlanDTO.builder()
                    .pplanId(id)
                    .prdplanId(hashmap.get(ProcurementPlanTableHead.PRDPLAN_ID.getLabel()))
                    .materialId(hashmap.get(ProcurementPlanTableHead.MAT_ID.getLabel()))
                    .ppmatQty(Integer.parseInt(hashmap.get(ProcurementPlanTableHead.PPMAT_QTY.getLabel())))
                    .pplanEnd(LocalDate.parse(hashmap.get(ProcurementPlanTableHead.PPLAN_END.getLabel()).substring(0, 10)))
                    .build();
        }).collect(Collectors.toList());

        log.info(procurementPlanDTOList.toString());
        return procurementPlanService.registerAll(procurementPlanDTOList);
    }

    @ResponseBody
    @PostMapping("/update/pplan")
    public StatusTuple updatePplan(@RequestBody ArrayList<HashMap<String, String>> list) {
        log.info(list.toString());

        if (list.isEmpty()) {
            return new StatusTuple(false, "PPLAN 수정사항은 없습니다.");
        }

        List<ProcurementPlanDTO> procurementPlanDTOList = list.stream().map(hashmap -> ProcurementPlanDTO.builder()
                .pplanId(hashmap.get(ProcurementPlanTableHead.PPLAN_ID.getLabel()))
                .prdplanId(hashmap.get(ProcurementPlanTableHead.PRDPLAN_ID.getLabel()))
                .materialId(hashmap.get(ProcurementPlanTableHead.MAT_ID.getLabel()))
                .ppmatQty(Integer.parseInt(hashmap.get(ProcurementPlanTableHead.PPMAT_QTY.getLabel())))
                .pplanEnd(LocalDate.parse(hashmap.get(ProcurementPlanTableHead.PPLAN_END.getLabel()).substring(0, 10)))
                .pplanStat(hashmap.get(ProcurementPlanTableHead.PPLAN_STAT.getLabel()))
                .build()
        ).collect(Collectors.toList());

        return procurementPlanService.updateAll(procurementPlanDTOList);
    }

    @ResponseBody
    @PostMapping("/view/pplan")
    public List<ProcurementPlanDTO> viewPplan(@RequestBody String str) {
        log.info("View PPlan: " + str);
        return procurementPlanService.viewAll();
    }

    @ResponseBody
    @PostMapping("/delete/pplan")
    public StatusTuple deletePplan(@RequestBody ArrayList<String> arrayList) {
        log.info("deleting ID: " + arrayList.toString());
        return procurementPlanService.deleteAll(arrayList);
    }
}
