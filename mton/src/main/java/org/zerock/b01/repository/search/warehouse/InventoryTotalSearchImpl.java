package org.zerock.b01.repository.search.warehouse;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.zerock.b01.domain.operation.QMaterial;
import org.zerock.b01.domain.warehouse.*;

import java.util.List;

public class InventoryTotalSearchImpl extends QuerydslRepositorySupport implements InventoryTotalSearch {

  public InventoryTotalSearchImpl() {super(InventoryTotal.class);}

  @Override
  public Page<InventoryTotal> searchInventoryByMaterial(String matId, String matType,
                                                           String matName, Pageable pageable) {

    QInventoryTotal inventoryTotal = QInventoryTotal.inventoryTotal;
    QMaterial material = QMaterial.material;

    JPQLQuery<InventoryTotal> query = from(inventoryTotal)
            .join(inventoryTotal.material, material);

    BooleanBuilder builder = new BooleanBuilder();

    if (matId != null && !matId.trim().isEmpty()) {
      builder.and(material.matId.contains(matId));
    }
    if (matType != null && !matType.trim().isEmpty()) {
      builder.and(material.matType.contains(matType));
    }
    if (matName != null && !matName.trim().isEmpty()) {
      builder.and(material.matName.contains(matName));
    }

    query.where(builder);
    this.getQuerydsl().applyPagination(pageable, query);

    List<InventoryTotal> result = query.fetch();
    long count = query.fetchCount();

    return new PageImpl<>(result, pageable, count);
  }

}
