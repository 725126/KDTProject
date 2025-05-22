package org.zerock.b01.repository.search.warehouse;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.zerock.b01.domain.operation.QMaterial;
import org.zerock.b01.domain.warehouse.*;

import java.time.LocalDate;
import java.util.List;

public class InventoryHistorySearchImpl extends QuerydslRepositorySupport implements InventoryHistorySearch {

  public InventoryHistorySearchImpl() {super(InventoryHistory.class);}

  @Override
  public Page<InventoryHistory> searchInventoryHistory(String matId, String matType, String matName,
                                                        LocalDate updateDateStart, LocalDate updateDateEnd,
                                                        String updateReason, Pageable pageable) {

    QInventoryHistory inventoryHistory = QInventoryHistory.inventoryHistory;
    QMaterial material = QMaterial.material;

    JPQLQuery<InventoryHistory> query = from(inventoryHistory)
            .join(inventoryHistory.inventory.material, material);



    BooleanBuilder builder = new BooleanBuilder();

    // 🔹 자재코드 검색
    if (matId != null && !matId.trim().isEmpty()) {
      builder.and(material.matId.contains(matId));
    }

    // 🔹 종별 검색
    if (matType != null && !matType.trim().isEmpty()) {
      builder.and(material.matType.contains(matType));
    }

    // 🔹 자재명 검색
    if (matName != null && !matName.trim().isEmpty()) {
      builder.and(material.matName.contains(matName));
    }

    // 🔹 변경일 범위 검색
    if (updateDateStart != null && updateDateEnd != null) {
      builder.and(inventoryHistory.updateDate
              .goe(updateDateStart.atStartOfDay()));
      builder.and(inventoryHistory.updateDate
              .lt(updateDateEnd.plusDays(1).atStartOfDay()));
    } else if (updateDateStart != null) {
      builder.and(inventoryHistory.updateDate
              .goe(updateDateStart.atStartOfDay()));
    } else if (updateDateEnd != null) {
      builder.and(inventoryHistory.updateDate
              .lt(updateDateEnd.plusDays(1).atStartOfDay()));
    }

    // 🔹 변경이유 검색
    if (updateReason != null && !updateReason.trim().isEmpty()) {
      builder.and(inventoryHistory.updateReason.eq(InventoryUpdateReason.valueOf(updateReason)));
    }


    // 🔹 조건 적용 및 페이징
    query.where(builder);
    this.getQuerydsl().applyPagination(pageable, query);

    List<InventoryHistory> result = query.fetch();
    long count = query.fetchCount();

    return new PageImpl<>(result, pageable, count);
  }
}
