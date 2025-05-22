package org.zerock.b01.service.warehouse;

import org.zerock.b01.domain.warehouse.CompanyStorage;
import org.zerock.b01.dto.warehouse.CompanyStorageDTO;

public interface CompanyStorageService {

  CompanyStorage createCompanyStorage(CompanyStorageDTO dto);
}
