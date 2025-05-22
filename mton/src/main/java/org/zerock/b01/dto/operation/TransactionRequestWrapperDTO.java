package org.zerock.b01.dto.operation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionRequestWrapperDTO {
    private List<PartnerTransactionDTO> partnerTransactions;
}
