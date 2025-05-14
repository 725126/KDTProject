package org.zerock.b01.service.warehouse;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.zerock.b01.domain.warehouse.DeliveryRequestItem;
import org.zerock.b01.domain.warehouse.IncomingStatus;
import org.zerock.b01.domain.warehouse.IncomingTotal;
import org.zerock.b01.repository.warehouse.DeliveryRequestItemRepository;
import org.zerock.b01.repository.warehouse.IncomingTotalRepository;

@Service
@RequiredArgsConstructor
@Log4j2
public class IncomingTotalServiceImpl implements IncomingTotalService {

  private final IncomingTotalRepository incomingTotalRepository;
  private final DeliveryRequestItemRepository deliveryRequestItemRepository;

  public void updateIncomingStatus(Long drItemId) {
    DeliveryRequestItem item = deliveryRequestItemRepository.findById(drItemId)
            .orElseThrow(() -> new IllegalArgumentException("해당 납입지시 항목이 존재하지 않습니다."));

    IncomingTotal incomingTotal = incomingTotalRepository.findByDeliveryRequestItem(item)
            .orElseThrow(() -> new IllegalStateException("해당 납입지시 항목에 대한 입고정보가 없습니다."));

    int deliveredQty = incomingTotal.getIncomingTotalQty();
    int returnQty = incomingTotal.getIncomingReturnTotalQty();
    int missingQty = incomingTotal.getIncomingMissingTotalQty();
    int totalQty = deliveredQty - returnQty - missingQty;
    int expectedQty = item.getDrItemQty();

    IncomingStatus newStatus;
    if (deliveredQty == 0) {
      newStatus = IncomingStatus.미입고;
    } else if (totalQty < expectedQty) {
      newStatus = IncomingStatus.부분입고;
    } else {
      newStatus = IncomingStatus.입고마감대기중;
    }

    incomingTotal.updateIncomingStatus(newStatus);  // 상태만 수정
    incomingTotalRepository.save(incomingTotal);   // 저장
  }


  public void closeIncoming(Long drItemId) {
    DeliveryRequestItem item = deliveryRequestItemRepository.findById(drItemId)
            .orElseThrow(() -> new IllegalArgumentException("해당 납입지시 항목이 존재하지 않습니다."));

    IncomingTotal incomingTotal = incomingTotalRepository.findByDeliveryRequestItem(item)
            .orElseThrow(() -> new IllegalStateException("입고 정보가 없습니다."));

    int deliveredQty = incomingTotal.getIncomingTotalQty();
    int returnQty = incomingTotal.getIncomingReturnTotalQty();
    int totalQty = deliveredQty - returnQty;
    int expectedQty = item.getDrItemQty();

    if (totalQty != expectedQty) {
      throw new IllegalStateException("전량 입고된 경우에만 마감할 수 있습니다.");
    }

    incomingTotal.markAsCompleted();
    incomingTotalRepository.save(incomingTotal);
  }

}
