package org.zerock.b01.service.warehouse;

import org.zerock.b01.domain.operation.Material;
import org.zerock.b01.domain.operation.PartnerStorage;
import org.zerock.b01.domain.user.Partner;
import org.zerock.b01.dto.PageRequestDTO;
import org.zerock.b01.dto.PageResponseDTO;
import org.zerock.b01.dto.warehouse.PartnerStorageDTO;

import java.util.List;

public interface PartnerStorageService {

  PartnerStorage getOrCreatePartnerStorage(Partner partner, Material material);

  PageResponseDTO<PartnerStorageDTO> listWithPartnerStorage(PageRequestDTO pageRequestDTO);

  void modifyStorage(List<PartnerStorageDTO> dtoList);

}
