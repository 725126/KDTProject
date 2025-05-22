package org.zerock.b01.repository.search.warehouse;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.CaseBuilder;
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

public class DeliveryPartnerSearchImpl extends QuerydslRepositorySupport implements DeliveryPartnerSearch {

  public DeliveryPartnerSearchImpl() {super(DeliveryPartner.class);}

  @Override
  public Page<DeliveryPartner> searchDeliveryPartnerAll(String drItemCode, String orderId, String matName,
                                                                LocalDate drItemDueDateStart, LocalDate drItemDueDateEnd,
                                                                Long partnerId, Pageable pageable) {

    QDeliveryPartner deliveryPartner = QDeliveryPartner.deliveryPartner;
    QDeliveryRequestItem deliveryRequestItem = QDeliveryRequestItem.deliveryRequestItem;
    QOrdering ordering = QOrdering.ordering;
    QContractMaterial contractMaterial = QContractMaterial.contractMaterial;
    QMaterial material = QMaterial.material;
    QPartner partner = QPartner.partner;

    JPQLQuery<DeliveryPartner> query = from(deliveryPartner)
            .join(deliveryPartner.deliveryRequestItem, deliveryRequestItem)
            .join(deliveryRequestItem.deliveryRequest.ordering, ordering)
            .join(ordering.contractMaterial, contractMaterial)
            .join(contractMaterial.material, material)
            .join(contractMaterial.contract.partner, partner);

    BooleanBuilder builder = new BooleanBuilder();

    // 🔹 납입코드 검색
    if (drItemCode != null && !drItemCode.trim().isEmpty()) {
      builder.and(deliveryRequestItem.drItemCode.contains(drItemCode));
    }

    // 🔹 발주번호 검색
    if (orderId != null && !orderId.trim().isEmpty()) {
      builder.and(ordering.orderId.contains(orderId));
    }

    // 🔹 자재명 검색
    if (matName != null && !matName.trim().isEmpty()) {
      builder.and(material.matName.contains(matName));
    }

    // 🔹 납입지시일자 범위 검색
    if (drItemDueDateStart != null && drItemDueDateEnd != null) {
      builder.and(deliveryRequestItem.drItemDueDate.between(drItemDueDateStart, drItemDueDateEnd));
    } else if (drItemDueDateStart != null) {
      builder.and(deliveryRequestItem.drItemDueDate.goe(drItemDueDateStart));
    } else if (drItemDueDateEnd != null) {
      builder.and(deliveryRequestItem.drItemDueDate.loe(drItemDueDateEnd));
    }

    if (partnerId != null) {
      builder.and(partner.partnerId.eq(partnerId));
    }

    // 🔹 조건 적용 및 페이징
    query.where(builder);
    query.orderBy(
            new CaseBuilder()
                    .when(deliveryPartner.deliveryPartnerStatus.eq(DeliveryPartnerStatus.완료)).then(1)
                    .otherwise(0)
                    .asc(),
            deliveryPartner.deliveryPartnerId.asc()
    );
    this.getQuerydsl().applyPagination(pageable, query);

    List<DeliveryPartner> result = query.fetch();
    long count = query.fetchCount();

    return new PageImpl<>(result, pageable, count);
  }
}
