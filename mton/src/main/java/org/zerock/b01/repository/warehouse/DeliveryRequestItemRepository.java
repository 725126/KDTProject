package org.zerock.b01.repository.warehouse;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.zerock.b01.domain.warehouse.DeliveryRequest;
import org.zerock.b01.domain.warehouse.DeliveryRequestItem;
import org.zerock.b01.repository.search.warehouse.DeliveryRequestItemSearch;

import java.time.LocalDate;
import java.util.List;

public interface DeliveryRequestItemRepository extends JpaRepository<DeliveryRequestItem, Long> , DeliveryRequestItemSearch {

  @Query("SELECT COALESCE(SUM(i.drItemQty), 0) FROM DeliveryRequestItem i WHERE i.deliveryRequest.drId = :drId")
  int getTotalQtyByDrId(@Param("drId") Long drId);

  @Query("SELECT d.drItemCode FROM DeliveryRequestItem d " +
          "WHERE d.drItemCode LIKE CONCAT(:prefix, '%') " +
          "ORDER BY d.drItemCode DESC")
  List<String> findTopByPrefix(@Param("prefix") String prefix, Pageable pageable);

  @Query("select r from DeliveryRequestItem r where r.deliveryRequest.drId = :drId")
  Page<DeliveryRequestItem> listDeliveryRequestItem(Long drId, Pageable pageable);


  @Query("SELECT MAX(d.drItemDueDate) FROM DeliveryRequestItem d WHERE d.deliveryRequest.drId = :drId")
  LocalDate findLastDrItemDueDateByDrId(@Param("drId") Long drId);

  List<DeliveryRequestItem> findByDeliveryRequest(DeliveryRequest deliveryRequest);;

}
