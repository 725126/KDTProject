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
import org.zerock.b01.domain.warehouse.DeliveryRequest;
import org.zerock.b01.domain.warehouse.DeliveryStatus;
import org.zerock.b01.domain.warehouse.QDeliveryRequest;

import java.time.LocalDate;
import java.util.List;

public class DeliveryRequestSearchImpl extends QuerydslRepositorySupport implements DeliveryRequestSearch {

  public DeliveryRequestSearchImpl() {super(DeliveryRequest.class);}

  @Override
  public Page<DeliveryRequest> searchDeliveryRequestAll(String orderId, String pCompany, String matId, String matName,
                                                 LocalDate orderDateStart, LocalDate orderDateEnd,
                                                 LocalDate orderEndStart, LocalDate orderEndEnd,
                                                 Pageable pageable) {

    QDeliveryRequest deliveryRequest = QDeliveryRequest.deliveryRequest;
    QOrdering ordering = QOrdering.ordering;
    QContractMaterial contractMaterial = QContractMaterial.contractMaterial;
    QMaterial material = QMaterial.material;
    QPartner partner = QPartner.partner;

    JPQLQuery<DeliveryRequest> query = from(deliveryRequest)
            .join(deliveryRequest.ordering, ordering)
            .join(ordering.contractMaterial, contractMaterial)
            .join(contractMaterial.material, material)
            .join(contractMaterial.contract.partner);


    BooleanBuilder builder = new BooleanBuilder();

    // 🔹 '취소' 상태인 생산계획 제외
    builder.and(ordering.orderStat.ne("취소"));

    // 🔹 발주번호 검색
    if (orderId != null && !orderId.trim().isEmpty()) {
      builder.and(ordering.orderId.contains(orderId));
    }
    // 🔹 협력사명 검색
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

    query.orderBy(
            new CaseBuilder()
                    .when(deliveryRequest.drStatus.eq(DeliveryStatus.완료)).then(1)
                    .otherwise(0)
                    .asc(),
            deliveryRequest.drId.asc()
    );
    this.getQuerydsl().applyPagination(pageable, query);

    List<DeliveryRequest> result = query.fetch();
    long count = query.fetchCount();

    return new PageImpl<>(result, pageable, count);
  }

}
