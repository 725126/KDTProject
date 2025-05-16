package org.zerock.b01.service.warehouse;

import org.zerock.b01.domain.warehouse.IncomingTotal;
import org.zerock.b01.dto.warehouse.IncomingTotalDTO;

public interface IncomingTotalService {

  void updateIncomingStatus(Long drItemId);

  void closeIncoming(Long incomingTotalId);

  IncomingTotalDTO readIncomingTotalOne(Long drItemId);

  default IncomingTotalDTO entityToIncomingTotalDto(IncomingTotal incomingTotal) {

    IncomingTotalDTO dto = IncomingTotalDTO.builder()
            .incomingTotalId(incomingTotal.getIncomingTotalId())
            .drItemCode(incomingTotal.getDeliveryRequestItem().getDrItemCode())
            .drItemDueDate(incomingTotal.getDeliveryRequestItem().getDrItemDueDate())
            .pCompany(incomingTotal.getDeliveryRequestItem().getDeliveryRequest()
                    .getOrdering().getContractMaterial().getContract()
                    .getPartner().getPCompany())
            .matName(incomingTotal.getDeliveryRequestItem().getDeliveryRequest()
                    .getOrdering().getContractMaterial().getMaterial().getMatName())
            .drItemQty(incomingTotal.getDeliveryRequestItem().getDrItemQty())
            .incomingEffectiveQty(incomingTotal.getIncomingEffectiveQty())
            .build();

    return dto;
  }

}
