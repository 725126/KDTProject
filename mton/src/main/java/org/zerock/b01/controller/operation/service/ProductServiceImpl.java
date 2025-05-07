package org.zerock.b01.controller.operation.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.zerock.b01.controller.operation.repository.ProductRepository;
import org.zerock.b01.domain.operation.Product;
import org.zerock.b01.domain.operation.StatusTuple;
import org.zerock.b01.dto.operation.ProductDTO;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;

    @Override
    public StatusTuple registerAll(List<ProductDTO> list) {
        var products = list.stream().map(prd -> Product.builder()
                .prodId(prd.getProdId())
                .prodName(prd.getProdName())
                .prodMeasure(prd.getProdMeasure())
                .prodUnit(prd.getProdUnit())
                .prodExplain(prd.getProdExplain())
                .build()
        ).collect(Collectors.toList());

        boolean hasDuple = false;
        boolean hasTwin = false;
        HashSet<String> twins = new HashSet<>();
        StringBuilder dupleMessage = new StringBuilder("다음 자재코드가 이미 존재합니다: ");
        StringBuilder twinMessage = new StringBuilder("다음 자재코드는 두 번 이상 입력되었습니다: ");

        for (var product : products) {
            String id = product.getProdId();
            log.info("넘어온값: " + id);

            if (productRepository.existsById(product.getProdId())) {
                hasDuple = true;
                dupleMessage.append(id).append(", ");
            }

            if (!twins.add(id)) {
                hasTwin = true;
                twinMessage.append(id).append(", ");
            }
        }

        if (hasDuple) {
            dupleMessage.append("상품 등록이 취소되었습니다.");
            return new StatusTuple(false, dupleMessage.toString());
        }

        if (hasTwin) {
            twinMessage.append("상품 등록이 취소되었습니다.");
            return new StatusTuple(false, twinMessage.toString());
        }

        productRepository.saveAll(products);
        return new StatusTuple(true, "모든 상품을 등록하였습니다.");
    }

    @Override
    public List<ProductDTO> viewAll() {
        return productRepository.findAll().stream().map(product ->
                ProductDTO.builder()
                        .prodId(product.getProdId())
                        .prodName(product.getProdName())
                        .prodMeasure(product.getProdMeasure())
                        .prodUnit(product.getProdUnit())
                        .prodExplain(product.getProdExplain())
                        .build()
                ).collect(Collectors.toList());
    }
}
