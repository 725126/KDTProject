package org.zerock.b01.repository.warehouse;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.zerock.b01.domain.operation.Transaction;
import org.zerock.b01.domain.user.Partner;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, String>, QuerydslPredicateExecutor<Transaction> {
    List<Transaction> findByPartner(Partner partner);

    Long countByTranIdStartingWith(String prefix);

    @Query("""
    SELECT SUM(t.totalAmount) FROM Transaction t
    WHERE t.partner = :partner
    AND FUNCTION('YEAR', t.tranDate) = :year
    AND FUNCTION('MONTH', t.tranDate) = :month
    """)
    Long getMonthlyTransactionTotal(@Param("partner") Partner partner,
                                    @Param("year") int year,
                                    @Param("month") int month);
}
