package org.zerock.b01.controller.operation.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.zerock.b01.controller.operation.repository.MaterialRepository;
import org.zerock.b01.controller.operation.repository.PbomRepository;
import org.zerock.b01.controller.operation.repository.ProductRepository;
import org.zerock.b01.domain.operation.Pbom;
import org.zerock.b01.domain.operation.StatusTuple;
import org.zerock.b01.dto.operation.PbomDTO;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
@Transactional
public class PbomServiceImpl implements PbomService {
    private final MaterialRepository materialRepository;
    private final ProductRepository productRepository;
    private final PbomRepository pbomRepository;

    @Override
    public StatusTuple registerAll(List<PbomDTO> list) {
        try {
            var pboms = list.stream().map(pbom -> Pbom.builder()
                    .pbomId(pbom.getPbomId())
                    .material(materialRepository.findById(pbom.getMatId()).get())
                    .product(productRepository.findById(pbom.getProdId()).get())
                    .pbomQty(Integer.parseInt(pbom.getPbomQty()))
                    .build()
            ).collect(Collectors.toList());


            boolean hasDuple = false;
            boolean hasTwin = false;
            boolean hasEqual = false;
            HashSet<String> twins = new HashSet<>();
            HashSet<String> equals = new HashSet<>();
            StringBuilder dupleMessage = new StringBuilder("다음 자재코드가 이미 존재합니다: ");
            StringBuilder twinMessage = new StringBuilder("다음 자재코드는 두 번 이상 입력되었습니다: ");
            StringBuilder equalMessage = new StringBuilder("다음 자재:상품 조합은 이미 존재합니다, 등록 대신 수정을 해 주세요: ");

            for (var pbom : pboms) {
                String prodIdp = pbom.getProduct().getProdId();
                String matIdp = pbom.getMaterial().getMatId();
                Optional<Pbom> pbomOptional = pbomRepository.findByProdAndMatId(prodIdp, matIdp);
                if (!equals.add(pbom.getMaterial().getMatId() + ":" + pbom.getProduct().getProdId()) || pbomOptional.isPresent()) {
                    hasEqual = true;
                }

                String id = pbom.getPbomId();
                log.info("넘어온값: " + id);

                if (pbomRepository.existsById(pbom.getPbomId())) {
                    hasDuple = true;
                    dupleMessage.append(id).append(", ");
                }

                if (!twins.add(id)) {
                    hasTwin = true;
                    twinMessage.append(id).append(", ");
                }
            }

            if (hasEqual) {
                equalMessage.append(equals);
                equalMessage.append("PBOM 등록이 취소되었습니다.");
                return new StatusTuple(false, equalMessage.toString());
            }

            if (hasDuple) {
                dupleMessage.append("PBOM 등록이 취소되었습니다.");
                return new StatusTuple(false, dupleMessage.toString());
            }

            if (hasTwin) {
                twinMessage.append("PBOM 등록이 취소되었습니다.");
                return new StatusTuple(false, twinMessage.toString());
            }

            pbomRepository.saveAll(pboms);
            return new StatusTuple(true, "모든 PBOM 을 등록하였습니다.");
        } catch (NoSuchElementException e) {
            return new StatusTuple(false, "존재하지 않는 자재코드 또는 상품코드를 입력했습니다.");
        }
    }

    // 현재는 등록된 그대로만 불러오지만 추가 DTO 구축으로 더 많은 정보를 불러올 수도 있음
    @Override
    public List<PbomDTO> viewAll() {
        return pbomRepository.findAll().stream().map(pbom ->
                PbomDTO.builder()
                        .pbomId(pbom.getPbomId())
                        .matId(pbom.getMaterial().getMatId())
                        .prodId(pbom.getProduct().getProdId())
                        .pbomQty(Integer.toString(pbom.getPbomQty()))
                        .build()
        ).collect(Collectors.toList());
    }

    @Override
    public StatusTuple deleteAll(ArrayList<String> arrayList) {
        try {
            pbomRepository.deleteAllById(arrayList);
            return new StatusTuple(true, "PBOM 삭제에 성공했습니다.");
        } catch (Exception e) {
            return new StatusTuple(false, "자재 삭제에 실패했습니다.");
        }
    }

    @Override
    public StatusTuple updateAll(List<PbomDTO> list) {
        try {
            var pboms = list.stream().map(pbom -> Pbom.builder()
                    .pbomId(pbom.getPbomId())
                    .material(materialRepository.findById(pbom.getMatId()).get())
                    .product(productRepository.findById(pbom.getProdId()).get())
                    .pbomQty(Integer.parseInt(pbom.getPbomQty()))
                    .build()
            ).collect(Collectors.toList());

            var pbomNames = list.stream().map(PbomDTO::getPbomId).collect(Collectors.toList());

            if (pboms.size() != pbomRepository.findAllById(pbomNames).size()) {
                return new StatusTuple(false, "PBOM 수정사항 개수가 일치하지 않습니다.");
            }

            pbomRepository.saveAll(pboms);
            return new StatusTuple(true, "모든 PBOM 수정사항을 반영하였습니다.");
        } catch (NoSuchElementException e) {
            return new StatusTuple(false, e.getMessage());
        }
    }
}
