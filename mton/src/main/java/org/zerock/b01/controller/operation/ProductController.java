package org.zerock.b01.controller.operation;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.zerock.b01.controller.operation.repository.MaterialRepository;
import org.zerock.b01.controller.operation.repository.PbomRepository;
import org.zerock.b01.controller.operation.repository.ProductRepository;
import org.zerock.b01.controller.operation.repository.ProductionPlanRepository;
import org.zerock.b01.controller.operation.service.MaterialService;
import org.zerock.b01.controller.operation.service.PbomService;
import org.zerock.b01.controller.operation.service.ProductService;
import org.zerock.b01.controller.operation.service.ProductionPlanService;
import org.zerock.b01.domain.operation.StatusTuple;
import org.zerock.b01.domain.operation.tablehead.MaterialTableHead;
import org.zerock.b01.domain.operation.tablehead.PbomTableHead;
import org.zerock.b01.domain.operation.tablehead.ProductTableHead;
import org.zerock.b01.domain.operation.tablehead.ProductionPlanTableHead;
import org.zerock.b01.dto.operation.MaterialDTO;
import org.zerock.b01.dto.operation.PbomDTO;
import org.zerock.b01.dto.operation.ProductDTO;
import org.zerock.b01.dto.operation.ProductionPlanDTO;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Controller
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/internal/product")
public class ProductController {
    private final MaterialService materialService;
    private final ProductService productService;
    private final PbomService pbomService;
    private final ProductionPlanService productionPlanService;

    private final MaterialRepository materialRepository;
    private final ProductRepository productRepository;
    private final PbomRepository pbomRepository;
    private final ProductionPlanRepository productionPlanRepository;

    // 품목 등록
    @GetMapping("/pbom")
    public String pbomGet(Model model) {
        model.addAttribute("materialTH", MaterialTableHead.values());
        model.addAttribute("productTH", ProductTableHead.values());
        model.addAttribute("pbomTH", PbomTableHead.values());
        return "/page/operation/product/pbom";
    }

    // 생산 계획
    @GetMapping("/prdplan")
    public String prdplanGet(Model model) {
        model.addAttribute("prdplanTH", ProductionPlanTableHead.values());
        return "/page/operation/product/prdplan";
    }

    @ResponseBody
    @PostMapping("/register/prdplan")
    public StatusTuple registerPrdPlan(@RequestBody ArrayList<HashMap<String, String>> list) {
        log.info(list.toString());

        if (list.isEmpty()) {
            return new StatusTuple(false, "등록할 생산계획이 없습니다.");
        }

        AtomicInteger atomicInteger = new AtomicInteger(1);

        List<ProductionPlanDTO> productionPlanDTOList = list.stream().map(hashmap -> {
            String id = hashmap.get(ProductionPlanTableHead.PRDPLAN_ID.getLabel());

            if (id == null || id.isEmpty()) {
                String count = productionPlanRepository.findLastOrderIdByPrefix("PRDPLAN");
                log.info(count);

                if (count != null) {
                    int countId = Integer.parseInt(count.substring(count.indexOf("PRDPLAN") + 7)) + atomicInteger.getAndIncrement();
                    id = "PRDPLAN" + String.format("%3d", countId).replace(" ", "0");
                } else {
                    int countId = atomicInteger.getAndIncrement();
                    id = "PRDPLAN" + String.format("%3d", countId).replace(" ", "0");
                }
            }

            return ProductionPlanDTO.builder()
                    .prdplanId(id)
                    .prodId(hashmap.get(ProductionPlanTableHead.PROD_ID.getLabel()))
                    .prdplanDate(LocalDate.now())
                    .prdplanEnd(LocalDate.parse(hashmap.get(ProductionPlanTableHead.PRDPLAN_END.getLabel()).substring(0, 10)))
                    .prdplanQty(Integer.parseInt(hashmap.get(ProductionPlanTableHead.PRDPLAN_QTY.getLabel())))
                    .build();
        }).collect(Collectors.toList());

        log.info(productionPlanDTOList.toString());

        return productionPlanService.registerAll(productionPlanDTOList);
    }

    // 자재 등록
    @ResponseBody
    @PostMapping("/register/mat")
    public StatusTuple registerMat(@RequestBody ArrayList<HashMap<String, String>> list) {
        log.info(list.toString());

        if (list.isEmpty()) {
            return new StatusTuple(false, "등록할 자재가 없습니다.");
        }

        AtomicInteger atomicInteger = new AtomicInteger(1);

        List<MaterialDTO> materialDTOList = list.stream().map(hashmap -> {
            String id = hashmap.get(MaterialTableHead.MAT_ID.getLabel());

            if (id == null || id.isEmpty()) {
                String count = materialRepository.findLastOrderIdByPrefix("EW");

                if (count != null) {
                    int countId = Integer.parseInt(count.substring(count.indexOf("EW") + 2)) + atomicInteger.getAndIncrement();
                    id = "EW" + String.format("%3d", countId).replace(" ", "0");
                } else {
                    int countId = atomicInteger.getAndIncrement();
                    id = "EW" + String.format("%3d", countId).replace(" ", "0");
                }
            }

            return MaterialDTO.builder()
                    .matId(id)
                    .matName(hashmap.get(MaterialTableHead.MAT_NAME.getLabel()))
                    .matType(hashmap.get(MaterialTableHead.MAT_TYPE.getLabel()))
                    .matMeasure(hashmap.get(MaterialTableHead.MAT_MEASURE.getLabel()))
                    .matUnit(hashmap.get(MaterialTableHead.MAT_UNIT.getLabel()))
                    .matExplain(hashmap.get(MaterialTableHead.MAT_EXPLAIN.getLabel()))
                    .build();
        }).collect(Collectors.toList());

        return materialService.registerAll(materialDTOList);
    }

    @ResponseBody
    @PostMapping("/register/prd")
    public StatusTuple registerPrd(@RequestBody ArrayList<HashMap<String, String>> list) {
        log.info(list.toString());

        if (list.isEmpty()) {
            return new StatusTuple(false, "등록할 상품이 없습니다.");
        }

        AtomicInteger atomicInteger = new AtomicInteger(1);

        List<ProductDTO> productDTOList = list.stream().map(hashmap -> {
            String id = hashmap.get(ProductTableHead.PROD_ID.getLabel());

            if (id == null || id.isEmpty()) {
                String count = productRepository.findLastOrderIdByPrefix("P");

                if (count != null) {
                    int countId = Integer.parseInt(count.substring(count.indexOf("P") + 1)) + atomicInteger.getAndIncrement();
                    id = "P" + String.format("%3d", countId).replace(" ", "0");
                } else {
                    int countId = atomicInteger.getAndIncrement();
                    id = "P" + String.format("%3d", countId).replace(" ", "0");
                }
            }

            return ProductDTO.builder()
                    .prodId(id)
                    .prodName(hashmap.get(ProductTableHead.PROD_NAME.getLabel()))
                    .prodMeasure(hashmap.get(ProductTableHead.PROD_MEASURE.getLabel()))
                    .prodUnit(hashmap.get(ProductTableHead.PROD_UNIT.getLabel()))
                    .prodExplain(hashmap.get(ProductTableHead.PROD_EXPLAIN.getLabel()))
                    .build();
        }).collect(Collectors.toList());

        return productService.registerAll(productDTOList);
    }

    @ResponseBody
    @PostMapping("/register/pbom")
    public StatusTuple registerPbom(@RequestBody ArrayList<HashMap<String, String>> list) {
        log.info(list.toString());

        if (list.isEmpty()) {
            return new StatusTuple(false, "등록할 PBOM 이 없습니다.");
        }

        AtomicInteger atomicInteger = new AtomicInteger(1);

        List<PbomDTO> pbomDTOList = list.stream().map(hashmap -> {
            String id = hashmap.get(PbomTableHead.PBOM_ID.getLabel());

            if (id == null || id.isEmpty()) {
                String count = pbomRepository.findLastOrderIdByPrefix("PBOM");
                if (count != null) {
                    int countId = Integer.parseInt(count.substring(count.indexOf("PBOM") + 4)) + atomicInteger.getAndIncrement();
                    id = "PBOM" + String.format("%3d", countId).replace(" ", "0");
                } else {
                    int countId = atomicInteger.getAndIncrement();
                    id = "PBOM" + String.format("%3d", countId).replace(" ", "0");
                }
            }

            return PbomDTO.builder()
                    .pbomId(id)
                    .matId(hashmap.get(PbomTableHead.MAT_ID.getLabel()))
                    .prodId(hashmap.get(PbomTableHead.PROD_ID.getLabel()))
                    .pbomQty(hashmap.get(PbomTableHead.PBOM_QTY.getLabel()))
                    .build();
        }).collect(Collectors.toList());

        return pbomService.registerAll(pbomDTOList);
    }

    @ResponseBody
    @PostMapping("/update/mat")
    public StatusTuple updateMat(@RequestBody ArrayList<HashMap<String, String>> list) {
        log.info(list.toString());

        if (list.isEmpty()) {
            return new StatusTuple(false, "자재 수정사항은 없습니다.");
        }

        List<MaterialDTO> materialDTOList = list.stream().map(hashmap -> MaterialDTO.builder()
                .matId(hashmap.get(MaterialTableHead.MAT_ID.getLabel()))
                .matName(hashmap.get(MaterialTableHead.MAT_NAME.getLabel()))
                .matType(hashmap.get(MaterialTableHead.MAT_TYPE.getLabel()))
                .matMeasure(hashmap.get(MaterialTableHead.MAT_MEASURE.getLabel()))
                .matUnit(hashmap.get(MaterialTableHead.MAT_UNIT.getLabel()))
                .matExplain(hashmap.get(MaterialTableHead.MAT_EXPLAIN.getLabel()))
                .build()).collect(Collectors.toList());

        return materialService.updateAll(materialDTOList);
    }

    @ResponseBody
    @PostMapping("/update/prd")
    public StatusTuple updatePrd(@RequestBody ArrayList<HashMap<String, String>> list) {
        log.info(list.toString());

        if (list.isEmpty()) {
            return new StatusTuple(false, "상품 수정사항은 없습니다.");
        }

        List<ProductDTO> productDTOList = list.stream().map(hashmap -> ProductDTO.builder()
                .prodId(hashmap.get(ProductTableHead.PROD_ID.getLabel()))
                .prodName(hashmap.get(ProductTableHead.PROD_NAME.getLabel()))
                .prodMeasure(hashmap.get(ProductTableHead.PROD_MEASURE.getLabel()))
                .prodUnit(hashmap.get(ProductTableHead.PROD_UNIT.getLabel()))
                .prodExplain(hashmap.get(ProductTableHead.PROD_EXPLAIN.getLabel()))
                .build()).collect(Collectors.toList());

        return productService.updateAll(productDTOList);
    }

    @ResponseBody
    @PostMapping("/update/pbom")
    public StatusTuple updatePbom(@RequestBody ArrayList<HashMap<String, String>> list) {
        log.info(list.toString());

        if (list.isEmpty()) {
            return new StatusTuple(false,"PBOM 수정사항은 없습니다.");
        }

        List<PbomDTO> pbomDTOList = list.stream().map(hashmap -> PbomDTO.builder()
                .pbomId(hashmap.get(PbomTableHead.PBOM_ID.getLabel()))
                .matId(hashmap.get(PbomTableHead.MAT_ID.getLabel()))
                .prodId(hashmap.get(PbomTableHead.PROD_ID.getLabel()))
                .pbomQty(hashmap.get(PbomTableHead.PBOM_QTY.getLabel()))
                .build()).collect(Collectors.toList());

        return pbomService.updateAll(pbomDTOList);
    }

    @ResponseBody
    @PostMapping("/update/prdplan")
    public StatusTuple updatePrdPlan(@RequestBody ArrayList<HashMap<String, String>> list) {
        log.info(list.toString());

        if (list.isEmpty()) {
            return new StatusTuple(false, "PRDPLAN 수정사항은 없습니다.");
        }

        List<ProductionPlanDTO> productionPlanDTOList = list.stream().map(hashmap -> ProductionPlanDTO.builder()
                .prdplanId(hashmap.get(ProductionPlanTableHead.PRDPLAN_ID.getLabel()))
                .prodId(hashmap.get(ProductionPlanTableHead.PROD_ID.getLabel()))
                .prdplanQty(Integer.parseInt(hashmap.get(ProductionPlanTableHead.PRDPLAN_QTY.getLabel())))
                .prdplanEnd(LocalDate.parse(hashmap.get(ProductionPlanTableHead.PRDPLAN_END.getLabel()).substring(0, 10)))
                .build()).collect(Collectors.toList());

        return productionPlanService.updateAll(productionPlanDTOList);
    }

    @ResponseBody
    @PostMapping("/view/mat")
    public List<MaterialDTO> viewMatTable(@RequestBody String str) {
        log.info("View Material: " + str);
        return materialService.viewAll();
    }

    @ResponseBody
    @PostMapping("/view/prd")
    public List<ProductDTO> viewPrdTable(@RequestBody String str) {
        log.info("View Product: " + str);
        return productService.viewAll();
    }

    @ResponseBody
    @PostMapping("/view/pbom")
    public List<PbomDTO> viewPbomTable(@RequestBody String str) {
        log.info("View Product: " + str);
        return pbomService.viewAll();
    }

    @ResponseBody
    @PostMapping("/view/prdplan")
    public List<ProductionPlanDTO> viewPrdPlanTable(@RequestBody String str) {
        log.info("View Product: " + str);
        return productionPlanService.viewAll();
    }

    @ResponseBody
    @PostMapping("/delete/mat")
    public StatusTuple deleteMatTable(@RequestBody ArrayList<String> arrayList) {
        log.info("deleting ID: " + arrayList.toString());
        return materialService.deleteAll(arrayList);
    }

    @ResponseBody
    @PostMapping("/delete/prd")
    public StatusTuple deletePrdTable(@RequestBody ArrayList<String> arrayList) {
        log.info("deleting ID: " + arrayList.toString());
        return productService.deleteAll(arrayList);
    }

    @ResponseBody
    @PostMapping("/delete/pbom")
    public StatusTuple deletePbomTable(@RequestBody ArrayList<String> arrayList) {
        log.info("deleting ID: " + arrayList.toString());
        return pbomService.deleteAll(arrayList);
    }

    @ResponseBody
    @PostMapping("/delete/prdplan")
    public StatusTuple deletePrdPlanTable(@RequestBody ArrayList<String> arrayList) {
        log.info("deleting ID: " + arrayList.toString());
        return productionPlanService.deleteAll(arrayList);
    }
}
