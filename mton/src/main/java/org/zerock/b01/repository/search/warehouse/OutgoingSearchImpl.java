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

public class OutgoingSearchImpl extends QuerydslRepositorySupport implements OutgoingSearch {

  public OutgoingSearchImpl() {super(Outgoing.class);}

  @Override
  public Page<Outgoing> searchOutgoing(LocalDate outgoingDateStart, LocalDate outgoingDateEnd,
                                                 String outgoingCode, String matId, String matName,
                                                 Pageable pageable) {

    QOutgoing outgoing = QOutgoing.outgoing;
    QMaterial material = QMaterial.material;

    JPQLQuery<Outgoing> query = from(outgoing)
            .join(outgoing.outgoingTotal.material, material);

    BooleanBuilder builder = new BooleanBuilder();

    // 🔹 일자 범위 검색
    if (outgoingDateStart != null && outgoingDateEnd != null) {
      builder.and(outgoing.outgoingDate
              .goe(outgoingDateStart.atStartOfDay()));
      builder.and(outgoing.outgoingDate
              .lt(outgoingDateEnd.plusDays(1).atStartOfDay()));
    } else if (outgoingDateStart != null) {
      builder.and(outgoing.outgoingDate
              .goe(outgoingDateStart.atStartOfDay()));
    } else if (outgoingDateEnd != null) {
      builder.and(outgoing.outgoingDate
              .lt(outgoingDateEnd.plusDays(1).atStartOfDay()));
    }

    // 🔹 입고코드 검색
    if (outgoingCode != null && !outgoingCode.trim().isEmpty()) {
      builder.and(outgoing.outgoingCode.contains(outgoingCode));
    }

    // 🔹 자재코드 검색
    if (matId != null && !matId.trim().isEmpty()) {
      builder.and(material.matId.contains(matId));
    }

    // 🔹 자재명 검색
    if (matName != null && !matName.trim().isEmpty()) {
      builder.and(material.matName.contains(matName));
    }

    // 🔹 조건 적용 및 페이징
    query.where(builder);
    this.getQuerydsl().applyPagination(pageable, query);

    List<Outgoing> result = query.fetch();
    long count = query.fetchCount();

    return new PageImpl<>(result, pageable, count);
  }
}
