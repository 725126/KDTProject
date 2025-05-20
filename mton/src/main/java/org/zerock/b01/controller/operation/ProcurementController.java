package org.zerock.b01.controller.operation;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.zerock.b01.controller.operation.repository.*;
import org.zerock.b01.controller.operation.service.ContractMaterialService;
import org.zerock.b01.controller.operation.service.InspectionService;
import org.zerock.b01.controller.operation.service.OrderingService;
import org.zerock.b01.controller.operation.service.ProcurementPlanService;
import org.zerock.b01.domain.operation.*;
import org.zerock.b01.domain.operation.tablehead.OrderingTableHead;
import org.zerock.b01.domain.operation.tablehead.ProcurementPlanTableHead;
import org.zerock.b01.dto.operation.*;
import org.zerock.b01.service.operation.ContractService;
import org.zerock.b01.dto.operation.ProcurementPlanDTO;
import org.zerock.b01.dto.partner.ContractMaterialDTO;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Controller
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/internal/procurement")
public class ProcurementController {
    private final ModelMapper modelMapper;
    private final ProcurementPlanRepository procurementPlanRepository;
    private final OrderingRepository orderingRepository;
    private final ProductionPlanRepository productionPlanRepository;
    private final PbomRepository pbomRepository;
    private final ContractMaterialRepository contractMaterialRepository;
    private final InspectionRepository inspectionRepository;

    private final ProcurementPlanService procurementPlanService;
    private final OrderingService orderingService;
    private final ContractMaterialService contractMaterialService;
    private final ContractService contractService;
    private final InspectionService inspectionService;

    //조달 계획
    @GetMapping("/procure")
    public String procureGet(Model model) {
        model.addAttribute("pplanTH", ProcurementPlanTableHead.values());
        model.addAttribute("prdplans", productionPlanRepository.findAll().stream().filter(x ->
                LocalDate.now().isBefore(x.getPrdplanEnd()) || LocalDate.now().isEqual(x.getPrdplanEnd())
        ).collect(Collectors.toList()));
        return "page/operation/procurement/procure";
    }

    // 계약 정보
    @GetMapping("/contract")
    public String contractGet(@RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "10") int size,
                              @RequestParam(required = false) String keyword,
                              @RequestParam(defaultValue = "newest") String sort,
                              Model model) {

        Sort sorting = switch (sort) {
            case "oldest" -> Sort.by("contract.conDate").ascending();
            default -> Sort.by("contract.conDate").descending(); // newest
        };

        Pageable pageable = PageRequest.of(page, size, sorting);

        Page<ContractMaterialViewDTO> contracts = contractService.getFilteredContracts(keyword, pageable);

        model.addAttribute("contracts", contracts.getContent());
        model.addAttribute("currentPage", contracts.getNumber() + 1);
        model.addAttribute("totalPages", contracts.getTotalPages());
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedSort", sort);
        model.addAttribute("selectedSize", size);


        return "page/operation/procurement/contract";
    }

    // 자재 발주
    @GetMapping("/order")
    public String orderGet(Model model) {
        model.addAttribute("orderTH", OrderingTableHead.values());
        model.addAttribute("pplans", procurementPlanRepository.findAll().stream().filter(x ->
                x.getPplanStat().equals("진행중")
        ).collect(Collectors.toList()));
        return "page/operation/procurement/order";
    }

    // 진척 검수
    @GetMapping("/inspect")
    public String inspectGet(Model model) {
        model.addAttribute("inspections", inspectionRepository.findAll());
        return "page/operation/procurement/inspect";
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
    @PostMapping("/register/order")
    public StatusTuple registerOrder(@RequestBody ArrayList<HashMap<String, String>> list) {
        log.info(list.toString());

        if (list.isEmpty()) {
            return new StatusTuple(false, "등록할 발주가 없습니다.");
        }

        AtomicInteger atomicInteger = new AtomicInteger(1);

        List<OrderingDTO> orderingDTOList = list.stream().map(hashmap -> {
            String id = hashmap.get(OrderingTableHead.ORDER_ID.getLabel());

            if (id == null || id.isEmpty()) {
                String count = orderingRepository.findLastOrderIdByPrefix("ORD");

                if (count != null) {
                    int countId = Integer.parseInt(count.substring(count.indexOf("ORD") + 3)) + atomicInteger.getAndIncrement();
                    id = "ORD" + String.format("%3d", countId).replace(" ", "0");
                } else {
                    int countId = atomicInteger.getAndIncrement();
                    id = "ORD" + String.format("%3d", countId).replace(" ", "0");
                }
            }

            return OrderingDTO.builder()
                    .orderId(id)
                    .cmtId(hashmap.get(OrderingTableHead.CMT_ID.getLabel()))
                    .pplanId(hashmap.get(OrderingTableHead.PPLAN_ID.getLabel()))
                    .orderQty(Integer.parseInt(hashmap.get(OrderingTableHead.ORDER_QTY.getLabel())))
                    .orderDate(LocalDate.parse(hashmap.get(OrderingTableHead.ORDER_DATE.getLabel()).substring(0, 10)))
                    .orderEnd(LocalDate.parse(hashmap.get(OrderingTableHead.ORDER_END.getLabel()).substring(0, 10)))
                    .orderStat(hashmap.get(OrderingTableHead.ORDER_STAT.getLabel()))
                    .build();
        }).collect(Collectors.toList());

        log.info(orderingDTOList.toString());

        StatusTuple result = orderingService.registerAll(orderingDTOList);

        // 장납기 자재들 검출
        var longReqs = orderingDTOList.stream().filter(x -> {
            Optional<ContractMaterial> cmat = contractMaterialRepository.findById(x.getCmtId());
            if (cmat.isPresent()) {
                return cmat.get().getCmtReq() >= 10;
            }
            return false;
        }).collect(Collectors.toList());

        log.info("longReqs: " + longReqs.toString());

        // 진척검수를 생성해야 하는가?
        if (!longReqs.isEmpty()) {
            inspectionService.registerByCmt(longReqs);
        }

        return result;
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
    @PostMapping("/update/order")
    public StatusTuple updateOrder(@RequestBody ArrayList<HashMap<String, String>> list) {
        log.info(list.toString());

        if (list.isEmpty()) {
            return new StatusTuple(false, "자재발주 수정사항은 없습니다.");
        }

        List<OrderingDTO> orderingDTOList = list.stream().map(hashmap -> OrderingDTO.builder()
                .orderId(hashmap.get(OrderingTableHead.ORDER_ID.getLabel()))
                .cmtId(hashmap.get(OrderingTableHead.CMT_ID.getLabel()))
                .pplanId(hashmap.get(OrderingTableHead.PPLAN_ID.getLabel()))
                .orderQty(Integer.parseInt(hashmap.get(OrderingTableHead.ORDER_QTY.getLabel())))
                .orderDate(LocalDate.parse(hashmap.get(OrderingTableHead.ORDER_DATE.getLabel()).substring(0, 10)))
                .orderEnd(LocalDate.parse(hashmap.get(OrderingTableHead.ORDER_END.getLabel()).substring(0, 10)))
                .orderStat(hashmap.get(OrderingTableHead.ORDER_STAT.getLabel()))
                .build()
        ).collect(Collectors.toList());

        return orderingService.updateAll(orderingDTOList);
    }

    @ResponseBody
    @PostMapping("/view/pplan")
    public List<ProcurementPlanDTO> viewPplan(@RequestBody String str) {
        log.info("View PPlan: " + str);
        return procurementPlanService.viewAll();
    }

    @ResponseBody
    @PostMapping("/view/order")
    public List<OrderingDTO> viewOrder(@RequestBody String str) {
        log.info("View Order: " + str);
        return orderingService.viewAll();
    }

    @ResponseBody
    @PostMapping("/view/contmat")
    public List<ContractMaterialDTO> viewContmat(@RequestBody String str) {
        log.info("View Contmat: " + str);
        return contractMaterialService.viewAll();
    }

    @ResponseBody
    @PostMapping("/delete/pplan")
    public StatusTuple deletePplan(@RequestBody ArrayList<String> arrayList) {
        log.info("deleting ID: " + arrayList.toString());
        return procurementPlanService.deleteAll(arrayList);
    }

    @ResponseBody
    @PostMapping("/delete/order")
    public StatusTuple deleteOrder(@RequestBody ArrayList<String> arrayList) {
        log.info("deleting ID: " + arrayList.toString());
        return orderingService.deleteAll(arrayList);
    }

    @ResponseBody
    @PostMapping("/calc/pplan")
    public List<CalcPbomDTO> calcPplan(@RequestBody String id) {
        Optional<ProductionPlan> temp = productionPlanRepository.findById(id);
        if (temp.isEmpty()) {
            return null;
        }

        Product product = temp.get().getProduct();
        var pboms = pbomRepository.findAllByProdId(product.getProdId());

        return pboms.stream().map(pbom -> CalcPbomDTO.builder()
                .matId(pbom.getMaterial().getMatId())
                .prdplanId(temp.get().getPrdplanId())
                .pbomQty(pbom.getPbomQty())
                .pbomMaxQty(pbom.getPbomQty() * temp.get().getPrdplanQty())
                .prdplanEnd(temp.get().getPrdplanEnd())
                .build()
        ).collect(Collectors.toList());
    }

    @ResponseBody
    @PostMapping("/calc/pplan/all")
    public List<CalcPbomDTO> calcPplanAll(@RequestBody List<String> ids) {
        List<CalcPbomDTO> calcPbomDTOList = new ArrayList<>();

        for (String id : ids) {
            Optional<ProductionPlan> temp = productionPlanRepository.findById(id);
            if (temp.isEmpty()) {
                return null;
            }

            Product product = temp.get().getProduct();
            var pboms = pbomRepository.findAllByProdId(product.getProdId());

            var pp = pboms.stream().map(pbom -> CalcPbomDTO.builder()
                    .matId(pbom.getMaterial().getMatId())
                    .prdplanId(temp.get().getPrdplanId())
                    .pbomQty(pbom.getPbomQty())
                    .pbomMaxQty(pbom.getPbomQty() * temp.get().getPrdplanQty())
                    .prdplanEnd(temp.get().getPrdplanEnd())
                    .build()
            ).collect(Collectors.toList());

            calcPbomDTOList.addAll(pp);
        }

        return calcPbomDTOList;
    }

    @ResponseBody
    @PostMapping("/calc/order")
    public List<CalcOrderingDTO> calcOrder(@RequestBody String id) {
        List<CalcOrderingDTO> calcOrderingDTOList = new ArrayList<>();
        Optional<ProcurementPlan> temp = procurementPlanRepository.findById(id);

        if (temp.isEmpty()) {
            return null;
        }

        Material material = temp.get().getMaterial();
        var cmts = contractMaterialRepository.findAllByMatId(material.getMatId());

        var ord = cmts.stream().filter(cmt -> LocalDate.now()
                .plusDays(cmt.getCmtReq()).isBefore(temp.get().getPplanEnd().plusDays(1)))
                .map(cmt -> CalcOrderingDTO.builder()
                        .pplanId(temp.get().getPplanId())
                        .cmtId(cmt.getCmtId())
                        .ppmatQty(temp.get().getPpmatQty())
                        .cmtReq(cmt.getCmtReq())
                        .pplanEnd(temp.get().getPplanEnd())
                        .build()
        ).collect(Collectors.toList());

        if (!ord.isEmpty()) {
            calcOrderingDTOList.add(ord.get(0));
        }

        return calcOrderingDTOList;
    }

    @ResponseBody
    @PostMapping("/calc/order/all")
    public List<CalcOrderingDTO> calcOrderAll(@RequestBody List<String> ids) {
        List<CalcOrderingDTO> calcOrderingDTOList = new ArrayList<>();
        for (String id : ids) {
            Optional<ProcurementPlan> temp = procurementPlanRepository.findById(id);
            if (temp.isEmpty()) {
                return null;
            }
            Material material = temp.get().getMaterial();

            var cmts = contractMaterialRepository.findAllByMatId(material.getMatId());
            var ords = cmts.stream().filter(cmt -> LocalDate.now()
                            .plusDays(cmt.getCmtReq()).isBefore(temp.get().getPplanEnd().plusDays(1)))
                    .map(cmt -> CalcOrderingDTO.builder()
                            .pplanId(temp.get().getPplanId())
                            .cmtId(cmt.getCmtId())
                            .ppmatQty(temp.get().getPpmatQty())
                            .cmtReq(cmt.getCmtReq())
                            .pplanEnd(temp.get().getPplanEnd())
                            .build()
                    ).collect(Collectors.toList());

            if (!ords.isEmpty()) {
                calcOrderingDTOList.add(ords.get(0));
            }
        }
        return calcOrderingDTOList;
    }
}
