package org.zerock.b01.repository.search.warehouse;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.zerock.b01.domain.operation.QMaterial;
import org.zerock.b01.domain.operation.QProductionPlan;
import org.zerock.b01.domain.warehouse.*;

import java.time.LocalDate;
import java.util.List;

public class OutgoingTotalSearchImpl extends QuerydslRepositorySupport implements OutgoingTotalSearch {

  public OutgoingTotalSearchImpl() {super(OutgoingTotal.class);}

  @Override
  public Page<OutgoingTotal> searchOutgoingTotal(LocalDate prdplanEndStart, LocalDate prdplanEndEnd,
                                                String prdplanId, String matId, String matName,
                                                String outgoingStatus, Pageable pageable) {

    QOutgoingTotal outgoingTotal = QOutgoingTotal.outgoingTotal;
    QProductionPlan productionPlan = QProductionPlan.productionPlan;
    QMaterial material = QMaterial.material;

    JPQLQuery<OutgoingTotal> query = from(outgoingTotal)
            .join(outgoingTotal.productionPlan, productionPlan)
            .join(outgoingTotal.material, material);

    BooleanBuilder builder = new BooleanBuilder();

    // 🔹 일자 범위 검색
    if (prdplanEndStart != null && prdplanEndEnd != null) {
      builder.and(productionPlan.prdplanEnd.between(prdplanEndStart, prdplanEndEnd));
    } else if (prdplanEndStart != null) {
      builder.and(productionPlan.prdplanEnd.goe(prdplanEndStart));
    } else if (prdplanEndEnd != null) {
      builder.and(productionPlan.prdplanEnd.loe(prdplanEndEnd));
    }

    // 🔹 입고코드 검색
    if (prdplanId != null && !prdplanId.trim().isEmpty()) {
      builder.and(productionPlan.prdplanId.contains(prdplanId));
    }

    // 🔹 자재코드 검색
    if (matId != null && !matId.trim().isEmpty()) {
      builder.and(material.matId.contains(matId));
    }

    // 🔹 자재명 검색
    if (matName != null && !matName.trim().isEmpty()) {
      builder.and(material.matName.contains(matName));
    }

    // 🔹 입고 상세 상태 검색
    if (outgoingStatus != null && !outgoingStatus.trim().isEmpty()) {
      builder.and(outgoingTotal.outgoingStatus.eq(OutgoingStatus.valueOf(outgoingStatus)));
    }


    // 🔹 조건 적용 및 페이징
    query.where(builder);
    this.getQuerydsl().applyPagination(pageable, query);

    List<OutgoingTotal> result = query.fetch();
    long count = query.fetchCount();

    return new PageImpl<>(result, pageable, count);
  }
}
