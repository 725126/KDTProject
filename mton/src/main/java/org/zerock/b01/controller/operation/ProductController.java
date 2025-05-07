package org.zerock.b01.controller.operation;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.zerock.b01.controller.operation.repository.MaterialRepository;
import org.zerock.b01.controller.operation.service.MaterialService;
import org.zerock.b01.controller.operation.service.ProductService;
import org.zerock.b01.domain.operation.StatusTuple;
import org.zerock.b01.domain.operation.tablehead.MaterialTableHead;
import org.zerock.b01.domain.operation.tablehead.PbomTableHead;
import org.zerock.b01.domain.operation.tablehead.ProductTableHead;
import org.zerock.b01.dto.operation.MaterialDTO;
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
    private final MaterialRepository materialRepository;

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
    @PostMapping("/view/mat")
    public List<MaterialDTO> viewMatTable(@RequestBody String str) {
        log.info("View Material: " + str);
        return materialService.viewAll();
    }

    @ResponseBody
    @PostMapping("/view/mat")
    public List<ProductDTO> viewPrdTable(@RequestBody String str) {
        log.info("View Product: " + str);
        return productService.viewAll();
    }
}
