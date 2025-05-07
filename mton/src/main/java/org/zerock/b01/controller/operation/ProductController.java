package org.zerock.b01.controller.operation;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.zerock.b01.controller.operation.repository.MaterialRepository;
import org.zerock.b01.controller.operation.repository.PbomRepository;
import org.zerock.b01.controller.operation.repository.ProductRepository;
import org.zerock.b01.controller.operation.service.MaterialService;
import org.zerock.b01.controller.operation.service.PbomService;
import org.zerock.b01.controller.operation.service.ProductService;
import org.zerock.b01.domain.operation.StatusTuple;
import org.zerock.b01.domain.operation.tablehead.MaterialTableHead;
import org.zerock.b01.domain.operation.tablehead.PbomTableHead;
import org.zerock.b01.domain.operation.tablehead.ProductTableHead;
import org.zerock.b01.dto.operation.MaterialDTO;
import org.zerock.b01.dto.operation.PbomDTO;
import org.zerock.b01.dto.operation.ProductDTO;

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
    private final MaterialRepository materialRepository;
    private final ProductRepository productRepository;
    private final PbomRepository pbomRepository;

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
    public String prdplanGet() {
        return "/page/operation/product/prdplan";
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
                int countId = Integer.parseInt(count.substring(count.indexOf("EW") + 2)) + atomicInteger.getAndIncrement();
                id = "EW" + String.format("%3d", countId).replace(" ", "0");
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
                int countId = Integer.parseInt(count.substring(count.indexOf("P") + 1)) + atomicInteger.getAndIncrement();
                id = "P" + String.format("%3d", countId).replace(" ", "0");
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
                int countId = Integer.parseInt(count.substring(count.indexOf("PBOM") + 4)) + atomicInteger.getAndIncrement();
                id = "PBOM" + String.format("%3d", countId).replace(" ", "0");
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
}
