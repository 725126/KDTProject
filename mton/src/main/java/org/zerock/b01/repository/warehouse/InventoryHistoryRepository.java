package org.zerock.b01.repository.warehouse;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.zerock.b01.domain.warehouse.InventoryHistory;
import org.zerock.b01.repository.search.warehouse.InventoryHistorySearch;

import java.util.List;

public interface InventoryHistoryRepository extends JpaRepository<InventoryHistory, Long>, InventoryHistorySearch {
    @Query("SELECT FUNCTION('WEEK', h.updateDate), SUM(h.changeQty * h.changePrice) " +
            "FROM InventoryHistory h " +
            "WHERE FUNCTION('YEAR', h.updateDate) = :year " +
            "GROUP BY FUNCTION('WEEK', h.updateDate) " +
            "ORDER BY FUNCTION('WEEK', h.updateDate)")
    List<Object[]> getWeeklyStockChangeAmount(@Param("year") int year);
}
