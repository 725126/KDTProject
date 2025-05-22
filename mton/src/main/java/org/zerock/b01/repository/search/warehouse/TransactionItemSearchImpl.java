package org.zerock.b01.repository.search.warehouse;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.zerock.b01.domain.operation.*;
import org.zerock.b01.domain.user.QPartner;
import org.zerock.b01.domain.warehouse.QDeliveryRequest;
import org.zerock.b01.domain.warehouse.QDeliveryRequestItem;
import org.zerock.b01.domain.warehouse.QIncomingTotal;
import org.zerock.b01.dto.warehouse.TransactionViewDTO;

import java.util.List;
import java.util.stream.Collectors;

public class TransactionItemSearchImpl
//        extends QuerydslRepositorySupport implements TransactionItemSearch
{

//  public TransactionItemSearchImpl() {super(Ordering.class);}

//  @Override
//  public Page<TransactionViewDTO> searchTransactionItem(String orderId, String pCompany, String matId,
//                                                        String matName, Pageable pageable) {
//
//    QIncomingTotal incomingTotal = QIncomingTotal.incomingTotal;
//    QDeliveryRequestItem deliveryRequestItem = QDeliveryRequestItem.deliveryRequestItem;
//    QDeliveryRequest deliveryRequest = QDeliveryRequest.deliveryRequest;
//    QOrdering ordering = QOrdering.ordering;
//    QContractMaterial contractMaterial = QContractMaterial.contractMaterial;
//    QMaterial material = QMaterial.material;
//    QPartner partner = QPartner.partner;
//
//
//    JPQLQuery<Tuple> query = from(incomingTotal)
//            .join(incomingTotal.deliveryRequestItem, deliveryRequestItem)
//            .join(deliveryRequestItem.deliveryRequest, deliveryRequest)
//            .join(deliveryRequest.ordering, ordering)
//            .join(ordering.contractMaterial, contractMaterial)
//            .join(contractMaterial.material, material)
//            .join(contractMaterial.contract.partner, partner)
//            .groupBy(ordering.orderId, partner.pCompany, partner.partnerId,
//                    material.matId, material.matName, contractMaterial.cmtPrice)
//            .select(
//                    ordering.orderId,
//                    partner.pCompany,
//                    partner.partnerId,
//                    material.matId,
//                    material.matName,
//                    contractMaterial.cmtPrice,
//                    incomingTotal.incomingEffectiveQty.sum(),
//                    incomingTotal.incomingEffectiveQty.multiply(contractMaterial.cmtPrice).sum()
//            );
//
//    BooleanBuilder builder = new BooleanBuilder();
//    builder.and(ordering.transIssued.isFalse());
//
//    // 발주 ID 검색
//    if (orderId != null && !orderId.trim().isEmpty()) {
//      builder.and(ordering.orderId.contains(orderId));
//    }
//
//    // 🔹 협력사 검색
//    if (pCompany != null && !pCompany.trim().isEmpty()) {
//      builder.and(partner.pCompany.contains(pCompany));
//    }
//
//    // 🔹 자재코드 검색
//    if (matId != null && !matId.trim().isEmpty()) {
//      builder.and(material.matId.contains(matId));
//    }
//
//    // 🔹 자재명 검색
//    if (matName != null && !matName.trim().isEmpty()) {
//      builder.and(material.matName.contains(matName));
//    }
//
//    builder.and(ordering.orderStat.eq("완료"));
//
//    query.where(builder);
//    this.getQuerydsl().applyPagination(pageable, query);
//
//    List<Tuple> tuples = query.fetch();
//
//    // ✅ Builder 방식으로 DTO 변환
//    List<TransactionViewDTO> result = tuples.stream()
//            .map(tuple -> TransactionViewDTO.builder()
//                    .orderId(tuple.get(ordering.orderId))
//                    .pCompany(tuple.get(partner.pCompany))
//                    .partnerId(tuple.get(partner.partnerId))
//                    .matId(tuple.get(material.matId))
//                    .matName(tuple.get(material.matName))
//                    .titemPrice(tuple.get(contractMaterial.cmtPrice))
//                    .titemQty(tuple.get(incomingTotal.incomingEffectiveQty.sum()))
//                    .amount(tuple.get(incomingTotal.incomingEffectiveQty.multiply(contractMaterial.cmtPrice).sum()))
//                    .build())
//            .collect(Collectors.toList());
//
//    long count = query.fetchCount();
//
//    return new PageImpl<>(result, pageable, count);
//  }
}
