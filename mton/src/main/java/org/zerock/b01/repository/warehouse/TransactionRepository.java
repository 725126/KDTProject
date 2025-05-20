package org.zerock.b01.repository.warehouse;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.b01.domain.operation.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, String> {
}
