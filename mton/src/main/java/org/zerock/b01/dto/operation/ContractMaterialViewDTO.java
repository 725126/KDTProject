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
public class ContractMaterialViewDTO {
    private String contractCode;
    private String partnerName;
    private String materialName;
    private int price;
    private int qty;
    private int leadTime;
    private LocalDate startDate;
    private LocalDate endDate;
    private String explain;
    private Long fileId; // 계약서 파일 보기용
}
