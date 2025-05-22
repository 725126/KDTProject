package org.zerock.b01.dto.warehouse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionListDTO {
    private String tranId;         // 거래코드
    private LocalDate tranDate;   // 거래일자
    private int totalAmount;      // 총금액
    private String companyName;   // 회사명 (협력업체명)
}
