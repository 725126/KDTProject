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


public class IncomingItemSearchImpl extends QuerydslRepositorySupport implements IncomingItemSearch {

  public IncomingItemSearchImpl() {super(IncomingItem.class);}

  @Override
  public Page<IncomingItem> searchIncomingItem(LocalDate modifyDateStart,
                                                 LocalDate modifyDateEnd, String incomingCode,
                                                 String pCompany, String matId, String matName,
                                                 String incomingItemStatus, Pageable pageable) {

    QIncomingItem incomingItem = QIncomingItem.incomingItem;
    QIncomingTotal incomingTotal = QIncomingTotal.incomingTotal;
    QIncoming incoming = QIncoming.incoming;
    QDeliveryPartnerItem deliveryPartnerItem = QDeliveryPartnerItem.deliveryPartnerItem;
    QDeliveryPartner deliveryPartner = QDeliveryPartner.deliveryPartner;
    QDeliveryRequestItem deliveryRequestItem = QDeliveryRequestItem.deliveryRequestItem;
    QDeliveryRequest deliveryRequest = QDeliveryRequest.deliveryRequest;
    QOrdering ordering = QOrdering.ordering;
    QContractMaterial contractMaterial = QContractMaterial.contractMaterial;
    QMaterial material = QMaterial.material;
    QPartner partner = QPartner.partner;

    JPQLQuery<IncomingItem> query = from(incomingItem)
            .join(incomingItem.incoming, incoming)
            .leftJoin(incoming.incomingTotal, incomingTotal)
            .join(incoming.deliveryPartnerItem, deliveryPartnerItem)
            .leftJoin(deliveryPartnerItem.deliveryPartner, deliveryPartner)
            .leftJoin(deliveryPartner.deliveryRequestItem, deliveryRequestItem)
            .leftJoin(deliveryRequestItem.deliveryRequest, deliveryRequest)
            .leftJoin(deliveryRequest.ordering, ordering)
            .leftJoin(ordering.contractMaterial, contractMaterial)
            .join(contractMaterial.material, material)
            .join(contractMaterial.contract.partner, partner);


    BooleanBuilder builder = new BooleanBuilder();

    // 🔹 처리일자 범위 검색
    if (modifyDateStart != null && modifyDateEnd != null) {
      builder.and(incomingItem.modifyDate
              .goe(modifyDateStart.atStartOfDay()));
      builder.and(deliveryPartnerItem.deliveryPartnerItemDate
              .lt(modifyDateEnd.plusDays(1).atStartOfDay()));
    } else if (modifyDateStart != null) {
      builder.and(incomingItem.modifyDate
              .goe(modifyDateStart.atStartOfDay()));
    } else if (modifyDateEnd != null) {
      builder.and(incomingItem.modifyDate
              .lt(modifyDateEnd.plusDays(1).atStartOfDay()));
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

    // 🔹 입고 상세 상태 검색
    if (incomingItemStatus != null && !incomingItemStatus.trim().isEmpty()) {
      builder.and(incomingItem.incomingItemStatus.eq(IncomingItemStatus.valueOf(incomingItemStatus)));
    }


    // 🔹 조건 적용 및 페이징
    query.where(builder);
    this.getQuerydsl().applyPagination(pageable, query);

    List<IncomingItem> result = query.fetch();
    long count = query.fetchCount();

    return new PageImpl<>(result, pageable, count);
  }

}
