package org.zerock.b01.dto.operation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionItemCreateDTO {
    private String orderId;
    private String matId;
    private int qty;
    private int price;
}
