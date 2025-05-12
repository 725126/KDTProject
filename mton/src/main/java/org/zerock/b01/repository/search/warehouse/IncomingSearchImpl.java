package org.zerock.b01.repository.search.warehouse;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.zerock.b01.domain.operation.QMaterial;
import org.zerock.b01.domain.user.QPartner;
import org.zerock.b01.domain.warehouse.*;

import java.time.LocalDate;
import java.util.List;

public class IncomingSearchImpl extends QuerydslRepositorySupport implements IncomingSearch {

  public IncomingSearchImpl() {super(Incoming.class);}

  @Override
  public Page<Incoming> searchIncoming(LocalDate deliveryPartnerItemDateStart,
                                       LocalDate deliveryPartnerItemDateEnd,
                                       String incomingCode, String pCompany,
                                       String matId, String matName,
                                       Pageable pageable) {

    QIncoming incoming = QIncoming.incoming;
    QDeliveryPartnerItem deliveryPartnerItem = QDeliveryPartnerItem.deliveryPartnerItem;
    QMaterial material = QMaterial.material;
    QPartner partner = QPartner.partner;

    JPQLQuery<Incoming> query = from(incoming)
            .join(incoming.deliveryPartnerItem, deliveryPartnerItem)
            .join(deliveryPartnerItem.deliveryPartner.deliveryRequestItem.
                    deliveryRequest.ordering.contractMaterial.material, material)
            .join(deliveryPartnerItem.deliveryPartner.deliveryRequestItem.deliveryRequest
                    .ordering.contractMaterial.contract.partner, partner);


    BooleanBuilder builder = new BooleanBuilder();

    // 🔹 납기일자 범위 검색
    if (deliveryPartnerItemDateStart != null && deliveryPartnerItemDateEnd != null) {
      builder.and(deliveryPartnerItem.deliveryPartnerItemDate
              .goe(deliveryPartnerItemDateStart.atStartOfDay()));
      builder.and(deliveryPartnerItem.deliveryPartnerItemDate
              .lt(deliveryPartnerItemDateEnd.plusDays(1).atStartOfDay()));
    } else if (deliveryPartnerItemDateStart != null) {
      builder.and(deliveryPartnerItem.deliveryPartnerItemDate
              .goe(deliveryPartnerItemDateStart.atStartOfDay()));
    } else if (deliveryPartnerItemDateEnd != null) {
      builder.and(deliveryPartnerItem.deliveryPartnerItemDate
              .lt(deliveryPartnerItemDateEnd.plusDays(1).atStartOfDay()));
    }

    // 🔹 입고코드 검색
    if (incomingCode != null && !incomingCode.trim().isEmpty()) {
      builder.and(incoming.incomingCode.contains(incomingCode));
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

    List<Incoming> result = query.fetch();
    long count = query.fetchCount();

    return new PageImpl<>(result, pageable, count);
  }
}
