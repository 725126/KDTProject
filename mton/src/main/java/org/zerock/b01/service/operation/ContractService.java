package org.zerock.b01.service.operation;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.zerock.b01.controller.operation.repository.ContractFileRepository;
import org.zerock.b01.controller.operation.repository.ContractMaterialRepository;
import org.zerock.b01.controller.operation.repository.ContractRepository;
import org.zerock.b01.domain.operation.Contract;
import org.zerock.b01.domain.operation.ContractFile;
import org.zerock.b01.domain.operation.ContractMaterial;
import org.zerock.b01.domain.user.Partner;
import org.zerock.b01.dto.operation.ContractMaterialViewDTO;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ContractService {

    private final ContractRepository contractRepository;
    private final ContractFileRepository contractFileRepository;
    private final ContractMaterialRepository contractMaterialRepository;

    public List<ContractMaterialViewDTO> getAllContractMaterialViews() {
        List<ContractMaterial> materials = contractMaterialRepository.findAllWithContractAndMaterial();

        return materials.stream().map(cm -> {
            Contract contract = cm.getContract();
            Partner partner = contract.getPartner();
            Optional<ContractFile> file = contractFileRepository.findByContract(contract);

            return ContractMaterialViewDTO.builder()
                    .contractCode(contract.getConId())
                    .partnerName(partner.getPCompany())
                    .materialName(cm.getMaterial() != null ? cm.getMaterial().getMatName() : "(삭제됨)")
                    .price(cm.getCmtPrice())
                    .qty(cm.getCmtQty())
                    .leadTime(cm.getCmtReq())
                    .startDate(contract.getConDate())
                    .endDate(contract.getConEnd())
                    .explain(cm.getCmtExplains())
                    .fileId(file.map(ContractFile::getFileId).orElse(null))
                    .build();
        }).toList();
    }

    public Page<ContractMaterialViewDTO> getFilteredContracts(String keyword, Pageable pageable) {
        Page<ContractMaterial> result = contractMaterialRepository.searchContractMaterials(keyword, pageable);

        return result.map(cm -> {
            Contract c = cm.getContract();
            Partner p = c.getPartner();
            Optional<ContractFile> file = contractFileRepository.findByContract(c);

            return ContractMaterialViewDTO.builder()
                    .contractCode(c.getConId())
                    .partnerName(p.getPCompany())
                    .materialName(cm.getMaterial() != null ? cm.getMaterial().getMatName() : "(삭제됨)")
                    .price(cm.getCmtPrice())
                    .qty(cm.getCmtQty())
                    .leadTime(cm.getCmtReq())
                    .startDate(c.getConDate())
                    .endDate(c.getConEnd())
                    .explain(cm.getCmtExplains())
                    .fileId(file.map(ContractFile::getFileId).orElse(null))
                    .build();
        });
    }

    public Page<ContractMaterialViewDTO> getContractsByPartner(Long partnerId, String keyword, Pageable pageable) {
        Page<ContractMaterial> materials = contractMaterialRepository.searchByPartner(partnerId, keyword, pageable);

        return materials.map(cm -> {
            Contract c = cm.getContract();
            Optional<ContractFile> file = contractFileRepository.findByContract(c);

            return ContractMaterialViewDTO.builder()
                    .contractCode(c.getConId())
                    .partnerName(c.getPartner().getPCompany())
                    .materialName(cm.getMaterial() != null ? cm.getMaterial().getMatName() : "(삭제됨)")
                    .price(cm.getCmtPrice())
                    .qty(cm.getCmtQty())
                    .leadTime(cm.getCmtReq())
                    .startDate(c.getConDate())
                    .endDate(c.getConEnd())
                    .explain(cm.getCmtExplains())
                    .fileId(file.map(ContractFile::getFileId).orElse(null))
                    .build();
        });
    }

    public Page<ContractMaterialViewDTO> getContractsByPartnerFiltered(Long partnerId, String keyword, String category, Pageable pageable) {
        if (!StringUtils.hasText(keyword)) keyword = null;
        if (!StringUtils.hasText(category)) category = null;

        Page<ContractMaterial> page = contractMaterialRepository.searchByPartnerWithFilter(partnerId, keyword, category, pageable);

        return page.map(cm -> {
            Contract c = cm.getContract();
            Optional<ContractFile> file = contractFileRepository.findByContract(c);

            return ContractMaterialViewDTO.builder()
                    .contractCode(c.getConId())
                    .partnerName(c.getPartner().getPCompany())
                    .materialName(cm.getMaterial() != null ? cm.getMaterial().getMatName() : "(삭제됨)")
                    .price(cm.getCmtPrice())
                    .qty(cm.getCmtQty())
                    .leadTime(cm.getCmtReq())
                    .startDate(c.getConDate())
                    .endDate(c.getConEnd())
                    .explain(cm.getCmtExplains())
                    .fileId(file.map(ContractFile::getFileId).orElse(null))
                    .build();
        });
    }




}
