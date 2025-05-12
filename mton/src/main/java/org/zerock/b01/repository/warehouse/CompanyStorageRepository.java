package org.zerock.b01.repository.warehouse;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.b01.domain.warehouse.CompanyStorage;

public interface CompanyStorageRepository extends JpaRepository<CompanyStorage, String> {
}
