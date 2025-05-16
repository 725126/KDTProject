package org.zerock.b01.controller.operation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.zerock.b01.domain.operation.Ordering;

public interface OrderingRepository extends JpaRepository<Ordering, String> {
    @Query(value = "SELECT o.orderId FROM Ordering o WHERE o.orderId LIKE ?1% ORDER BY o.orderId DESC LIMIT 1")
    String findLastOrderIdByPrefix(String prefix);
}
