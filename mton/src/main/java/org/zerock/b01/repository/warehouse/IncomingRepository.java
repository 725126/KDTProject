package org.zerock.b01.repository.warehouse;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.zerock.b01.domain.warehouse.Incoming;
import org.zerock.b01.repository.search.warehouse.IncomingSearch;

import java.util.List;

public interface IncomingRepository extends JpaRepository<Incoming, Long>, IncomingSearch {

  @Query("SELECT d.incomingCode FROM Incoming d " +
          "WHERE d.incomingCode LIKE CONCAT(:prefix, '%') " +
          "ORDER BY d.incomingCode DESC")
  List<String> findIncomingTopByPrefix(@Param("prefix") String prefix, Pageable pageable);

  @Query("select r from Incoming r where r.incomingTotal.incomingTotalId = :incomingTotalId")
  Page<Incoming> listIncoming(Long incomingTotalId, Pageable pageable);
}
