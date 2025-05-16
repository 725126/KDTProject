package org.zerock.b01.repository.search.warehouse;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.zerock.b01.domain.operation.QContractMaterial;
import org.zerock.b01.domain.operation.QMaterial;
import org.zerock.b01.domain.operation.QOrdering;
import org.zerock.b01.domain.user.QPartner;
import org.zerock.b01.domain.warehouse.*;

import java.time.LocalDate;
import java.util.List;

public class IncomingTotalSearchImpl extends QuerydslRepositorySupport implements IncomingTotalSearch {

  public IncomingTotalSearchImpl() {super(IncomingTotal.class);}

  @Override
  public Page<IncomingTotal> searchIncomingTotal(LocalDate incomingCompletedAtStart,
                                       LocalDate incomingCompletedAtEnd, String pCompany,
                                       String matId, String matName,
                                       Pageable pageable) {

    QIncomingTotal incomingTotal = QIncomingTotal.incomingTotal;
    QDeliveryRequestItem deliveryRequestItem = QDeliveryRequestItem.deliveryRequestItem;
    QDeliveryRequest deliveryRequest = QDeliveryRequest.deliveryRequest;
    QOrdering ordering = QOrdering.ordering;
    QContractMaterial contractMaterial = QContractMaterial.contractMaterial;
    QMaterial material = QMaterial.material;
    QPartner partner = QPartner.partner;

    JPQLQuery<IncomingTotal> query = from(incomingTotal)
            .join(incomingTotal.deliveryRequestItem, deliveryRequestItem)
            .leftJoin(deliveryRequestItem.deliveryRequest, deliveryRequest)
            .leftJoin(deliveryRequest.ordering, ordering)
            .leftJoin(ordering.contractMaterial, contractMaterial)
            .join(contractMaterial.material, material)
            .join(contractMaterial.contract.partner, partner);


    BooleanBuilder builder = new BooleanBuilder();

    builder.and(incomingTotal.incomingCompletedAt.isNotNull());

    // 🔹 입고예정일자 범위 검색
    if (incomingCompletedAtStart != null && incomingCompletedAtEnd != null) {
      builder.and(incomingTotal.incomingCompletedAt
              .goe(incomingCompletedAtStart.atStartOfDay()));
      builder.and(incomingTotal.incomingCompletedAt
              .lt(incomingCompletedAtEnd.plusDays(1).atStartOfDay()));
    } else if (incomingCompletedAtStart != null) {
      builder.and(incomingTotal.incomingCompletedAt
              .goe(incomingCompletedAtStart.atStartOfDay()));
    } else if (incomingCompletedAtEnd != null) {
      builder.and(incomingTotal.incomingCompletedAt
              .lt(incomingCompletedAtEnd.plusDays(1).atStartOfDay()));
    }

    // 🔹 협력사 검색
    if (pCompany != null && !pCompany.trim().isEmpty()) {
      builder.and(partner.pCompany.contains(pCompany));
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

    List<IncomingTotal> result = query.fetch();
    long count = query.fetchCount();

    return new PageImpl<>(result, pageable, count);
  }
}
