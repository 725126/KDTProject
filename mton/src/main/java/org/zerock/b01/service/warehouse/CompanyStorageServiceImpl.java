package org.zerock.b01.service.warehouse;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.zerock.b01.domain.warehouse.CompanyStorage;
import org.zerock.b01.dto.warehouse.CompanyStorageDTO;
import org.zerock.b01.repository.warehouse.CompanyStorageRepository;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional
public class CompanyStorageServiceImpl implements CompanyStorageService {

  private final CompanyStorageRepository companyStorageRepository;

  public CompanyStorage createCompanyStorage(CompanyStorageDTO dto) {

    if (companyStorageRepository.existsById(dto.getCstorageId())) {
      throw new IllegalArgumentException("이미 존재하는 창고 ID입니다.");
    }

    CompanyStorage companyStorage = CompanyStorage.builder()
            .cstorageId(dto.getCstorageId())
            .cstorageAddress(dto.getCstorageAddress())
            .cstorageContactNumber(dto.getCstorageContactNumber())
            .cstorageManager(dto.getCstorageManager())
            .build();

    return companyStorageRepository.save(companyStorage);
  }
}
