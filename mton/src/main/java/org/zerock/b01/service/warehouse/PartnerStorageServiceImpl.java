package org.zerock.b01.service.warehouse;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.zerock.b01.domain.operation.Material;
import org.zerock.b01.domain.operation.PartnerStorage;
import org.zerock.b01.domain.user.Partner;
import org.zerock.b01.dto.PageRequestDTO;
import org.zerock.b01.dto.PageResponseDTO;
import org.zerock.b01.dto.warehouse.PartnerStorageDTO;
import org.zerock.b01.repository.warehouse.PartnerStorageRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class PartnerStorageServiceImpl implements PartnerStorageService {

  private final PartnerStorageRepository partnerStorageRepository;

  @Override
  public PartnerStorage getOrCreatePartnerStorage(Partner partner, Material material) {

    if (partner == null || material == null) {
      throw new IllegalArgumentException("Partner or Material must not be null");
    }

    return partnerStorageRepository.findByPartnerAndMaterial(partner,material)
            .orElseGet(() -> {
              PartnerStorage newStorage = PartnerStorage.builder()
                      .partner(partner)
                      .material(material)
                      .sstorageQty(0)
                      .build();
              return partnerStorageRepository.save(newStorage);
            });
  }

  public PageResponseDTO<PartnerStorageDTO> listWithPartnerStorage(PageRequestDTO pageRequestDTO,Long partnerId) {

    Pageable pageable = pageRequestDTO.getPageable("pstorageId");

    Page<PartnerStorage> result = partnerStorageRepository.findByPartner_PartnerId(partnerId, pageable);


    List<PartnerStorageDTO> dtoList = result.getContent().stream()
            .map(partnerStorage -> PartnerStorageDTO.builder()
                    .pstorageId(partnerStorage.getPstorageId())
                    .matId(partnerStorage.getMaterial().getMatId())
                    .matName(partnerStorage.getMaterial().getMatName())
                    .sstorageQty(partnerStorage.getSstorageQty())
                    .build())
            .toList();

    return PageResponseDTO.<PartnerStorageDTO>withAll()
            .pageRequestDTO(pageRequestDTO)
            .dtoList(dtoList)
            .total((int) result.getTotalElements())
            .build();
  }

  @Override
  public void modifyStorage(List<PartnerStorageDTO> dtoList) {
    for (PartnerStorageDTO dto : dtoList) {
      PartnerStorage partnerStorage = partnerStorageRepository.findById(dto.getPstorageId())
              .orElseThrow(() -> new IllegalArgumentException("해당 재고 없음"));

      partnerStorage.changeStorageQty(dto.getSstorageQty()); // 핵심 수정 메서드
    }
  }

}
