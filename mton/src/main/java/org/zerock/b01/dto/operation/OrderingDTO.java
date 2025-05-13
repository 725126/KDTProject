package org.zerock.b01.dto.operation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderingDTO {
    private String orderId;
    private String cmtId;
    private String pplanId;
    private int orderQty;
    private LocalDate orderDate;
    private LocalDate orderEnd;
    private String orderStat;
}
