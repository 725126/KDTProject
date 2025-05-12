package org.zerock.b01.repository.search.warehouse;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.zerock.b01.domain.operation.QMaterial;
import org.zerock.b01.domain.operation.QOrdering;
import org.zerock.b01.domain.warehouse.DeliveryRequest;
import org.zerock.b01.domain.warehouse.QDeliveryRequest;

import java.time.LocalDate;
import java.util.List;

public class DeliveryRequestSearchImpl extends QuerydslRepositorySupport implements DeliveryRequestSearch {

  public DeliveryRequestSearchImpl() {super(DeliveryRequest.class);}

  @Override
  public Page<DeliveryRequest> searchDeliveryRequestAll(String orderId, String matName,
                                                 LocalDate orderDateStart, LocalDate orderDateEnd,
                                                 LocalDate orderEndStart, LocalDate orderEndEnd,
                                                 Pageable pageable) {

    QDeliveryRequest deliveryRequest = QDeliveryRequest.deliveryRequest;
    QOrdering ordering = QOrdering.ordering;
    QMaterial material = QMaterial.material;

    JPQLQuery<DeliveryRequest> query = from(deliveryRequest)
            .join(deliveryRequest.ordering, ordering)
            .join(ordering.contractMaterial.material, material);

    BooleanBuilder builder = new BooleanBuilder();

    // 🔹 발주번호 검색
    if (orderId != null && !orderId.trim().isEmpty()) {
      builder.and(ordering.orderId.contains(orderId));
    }

    // 🔹 자재명 검색
    if (matName != null && !matName.trim().isEmpty()) {
      builder.and(material.matName.contains(matName));
    }

    // 🔹 발주일자 범위 검색
    if (orderDateStart != null && orderDateEnd != null) {
      builder.and(ordering.orderDate.between(orderDateStart, orderDateEnd)); // LocalDate 범위 설정
    } else if (orderDateStart != null) {
      builder.and(ordering.orderDate.goe(orderDateStart)); // orderDateStart 이상의 값
    } else if (orderDateEnd != null) {
      builder.and(ordering.orderDate.loe(orderDateEnd)); // orderDateEnd의 다음 날까지 포함
    }

    // 🔹 납기일자 범위 검색
    if (orderEndStart != null && orderEndEnd != null) {
      builder.and(ordering.orderEnd.between(orderEndStart, orderEndEnd));
    } else if (orderEndStart != null) {
      builder.and(ordering.orderEnd.goe(orderEndStart));
    } else if (orderEndEnd != null) {
      builder.and(ordering.orderEnd.loe(orderEndEnd));
    }

    // 🔹 조건 적용 및 페이징
    query.where(builder);
    this.getQuerydsl().applyPagination(pageable, query);

    List<DeliveryRequest> result = query.fetch();
    long count = query.fetchCount();

    return new PageImpl<>(result, pageable, count);
  }

}
