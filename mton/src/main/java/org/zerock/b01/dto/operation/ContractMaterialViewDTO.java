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
    private String contractCode; // 계약코드
    private String partnerName; // 협력업체 회사명
    private String materialName; // 자재명
    private int price; // 단가
    private int qty; // 수량
    private int leadTime; // 리드타임
    private LocalDate startDate; // 계약일
    private LocalDate endDate; // 만료일
    private String explain; // 비고(합의내용)
    private Long fileId; // 계약서 파일 보기용
}
