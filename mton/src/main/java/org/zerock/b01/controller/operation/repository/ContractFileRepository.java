package org.zerock.b01.controller.operation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.b01.domain.operation.Contract;
import org.zerock.b01.domain.operation.ContractFile;

import java.util.Optional;

public interface ContractFileRepository extends JpaRepository<ContractFile, Long> {

    Optional<ContractFile> findByContract(Contract contract);
}
