package org.zerock.b01.controller.operation.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.zerock.b01.controller.operation.repository.ContractMaterialRepository;
import org.zerock.b01.domain.operation.StatusTuple;
import org.zerock.b01.dto.partner.ContractMaterialDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
@Transactional
public class ContractMaterialServiceImpl implements ContractMaterialService {
    private final ContractMaterialRepository contractMaterialRepository;

    @Override
    public StatusTuple registerAll(List<ContractMaterialDTO> list) {
        return null;
    }

    @Override
    public List<ContractMaterialDTO> viewAll() {
        return contractMaterialRepository.findAll().stream().map(conmat ->
                ContractMaterialDTO.builder()
                        .cmtId(conmat.getCmtId())
                        .conId(conmat.getContract().getConId())
                        .matId(conmat.getMaterial().getMatId())
                        .cmtPrice(conmat.getCmtPrice())
                        .cmtReq(conmat.getCmtReq())
                        .build()
        ).collect(Collectors.toList());
    }

    @Override
    public StatusTuple deleteAll(ArrayList<String> arrayList) {
        return null;
    }

    @Override
    public StatusTuple updateAll(List<ContractMaterialDTO> list) {
        return null;
    }
}
