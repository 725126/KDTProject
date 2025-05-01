package org.zerock.b01.controller.operation;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.zerock.b01.domain.operation.tablehead.MaterialTableHead;
import org.zerock.b01.domain.operation.tablehead.PbomTableHead;
import org.zerock.b01.domain.operation.tablehead.ProductTableHead;
import org.zerock.b01.dto.operation.MaterialDTO;

import java.util.ArrayList;
import java.util.HashMap;

@Controller
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/internal/product")
public class ProductController {
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

    // 뭐가 잘 되는지 테스트용
    @ResponseBody
    @PostMapping("/test")
    public ArrayList<MaterialDTO> test(@RequestBody ArrayList<HashMap<String, Object>> list) {
        log.info(list.toString());
        ArrayList<MaterialDTO> materials = new ArrayList<>();
        for (HashMap<String, Object> map : list) {
            MaterialDTO materialDTO = MaterialDTO.builder()
                    .matId((String) map.get(MaterialTableHead.MAT_ID.getLabel()))
                    .matName((String) map.get(MaterialTableHead.MAT_NAME.getLabel()))
                    .matType((String) map.get(MaterialTableHead.MAT_TYPE.getLabel()))
                    .matMeasure((String) map.get(MaterialTableHead.MAT_MEASURE.getLabel()))
                    .matUnit((String) map.get(MaterialTableHead.MAT_UNIT.getLabel()))
                    .matExplain((String) map.get(MaterialTableHead.MAT_EXPLAIN.getLabel()))
                    .build();
            materials.add(materialDTO);
        }
        return materials;
    }
}
