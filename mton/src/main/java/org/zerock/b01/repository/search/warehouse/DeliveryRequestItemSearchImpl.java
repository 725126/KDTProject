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
import org.zerock.b01.domain.warehouse.DeliveryRequestItem;
import org.zerock.b01.domain.warehouse.QDeliveryRequestItem;

import java.time.LocalDate;
import java.util.List;

public class DeliveryRequestItemSearchImpl extends QuerydslRepositorySupport implements DeliveryRequestItemSearch {

  public DeliveryRequestItemSearchImpl() {super(DeliveryRequestItem.class);}

  @Override
  public Page<DeliveryRequestItem> searchDeliveryRequestItemAll(String drItemCode, String orderId, String matName,
                                                                LocalDate orderEndStart, LocalDate orderEndEnd,
                                                                LocalDate drItemDueDateStart, LocalDate drItemDueDateEnd,
                                                                LocalDate creDateStart, LocalDate creDateEnd,
                                                                Pageable pageable) {

    QDeliveryRequestItem deliveryRequestItem = QDeliveryRequestItem.deliveryRequestItem;
    QOrdering ordering = QOrdering.ordering;
    QMaterial material = QMaterial.material;

    JPQLQuery<DeliveryRequestItem> query = from(deliveryRequestItem)
            .join(deliveryRequestItem.deliveryRequest.ordering, ordering)
            .join(ordering.contractMaterial.material, material);

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


    // 🔹 납기일자 범위 검색
    if (orderEndStart != null && orderEndEnd != null) {
      builder.and(ordering.orderEnd.between(orderEndStart, orderEndEnd.plusDays(1)));
    } else if (orderEndStart != null) {
      builder.and(ordering.orderEnd.goe(orderEndStart));
    } else if (orderEndEnd != null) {
      builder.and(ordering.orderEnd.loe(orderEndEnd));
    }

    // 🔹 납입지시일자 범위 검색
    if (drItemDueDateStart != null && drItemDueDateEnd != null) {
      builder.and(deliveryRequestItem.drItemDueDate.between(drItemDueDateStart, drItemDueDateEnd));
    } else if (drItemDueDateStart != null) {
      builder.and(deliveryRequestItem.drItemDueDate.goe(drItemDueDateStart));
    } else if (drItemDueDateEnd != null) {
      builder.and(deliveryRequestItem.drItemDueDate.loe(drItemDueDateEnd));
    }

    if (creDateStart != null && creDateEnd != null) {
      builder.and(deliveryRequestItem.creDate.goe(creDateStart.atStartOfDay()));
      builder.and(deliveryRequestItem.creDate.lt(creDateEnd.plusDays(1).atStartOfDay()));
    } else if (creDateStart != null) {
      builder.and(deliveryRequestItem.creDate.goe(creDateStart.atStartOfDay()));
    } else if (creDateEnd != null) {
      builder.and(deliveryRequestItem.creDate.lt(creDateEnd.plusDays(1).atStartOfDay()));
    }

    // 🔹 조건 적용 및 페이징
    query.where(builder);
    this.getQuerydsl().applyPagination(pageable, query);

    List<DeliveryRequestItem> result = query.fetch();
    long count = query.fetchCount();

    return new PageImpl<>(result, pageable, count);
  }
}
