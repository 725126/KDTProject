package org.zerock.b01.controller.operation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.b01.domain.operation.Contract;

public interface ContractRepository extends JpaRepository<Contract, Long> {
}
