package org.zerock.b01.controller.operation.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.zerock.b01.controller.operation.repository.MaterialRepository;
import org.zerock.b01.domain.operation.Material;
import org.zerock.b01.domain.operation.StatusTuple;
import org.zerock.b01.domain.operation.tablehead.MaterialTableHead;
import org.zerock.b01.dto.operation.MaterialDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
@Transactional
public class MaterialServiceImpl implements MaterialService {
    private final MaterialRepository materialRepository;

    @Override
    public StatusTuple registerAll(List<MaterialDTO> list) {
        var materials = list.stream().map(mat -> Material.builder()
                .matId(mat.getMatId())
                .matName(mat.getMatName())
                .matType(mat.getMatType())
                .matMeasure(mat.getMatMeasure())
                .matUnit(mat.getMatUnit())
                .matExplain(mat.getMatExplain())
                .build()).collect(Collectors.toList());

        boolean hasDuple = false;
        boolean hasTwin = false;
        HashSet<String> twins = new HashSet<>();
        StringBuilder dupleMessage = new StringBuilder("다음 자재코드가 이미 존재합니다: ");
        StringBuilder twinMessage = new StringBuilder("다음 자재코드는 두 번 이상 입력되었습니다: ");

        for (var material : materials) {
            String id = material.getMatId();
            log.info("넘어온값: " + id);

            if (materialRepository.existsById(material.getMatId())) {
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

        materialRepository.saveAll(materials);
        return new StatusTuple(true, "모든 자재를 등록하였습니다.");
    }

    @Override
    public List<MaterialDTO> viewAll() {

        return materialRepository.findAll().stream().map(material ->
                MaterialDTO.builder()
                    .matId(material.getMatId())
                    .matName(material.getMatName())
                    .matType(material.getMatType())
                    .matMeasure(material.getMatMeasure())
                    .matUnit(material.getMatUnit())
                    .matExplain(material.getMatExplain())
                    .build()
        ).collect(Collectors.toList());
    }
}
