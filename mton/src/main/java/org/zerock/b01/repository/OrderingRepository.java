package org.zerock.b01.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.b01.domain.operation.Ordering;

public interface OrderingRepository extends JpaRepository<Ordering,String> {
}
