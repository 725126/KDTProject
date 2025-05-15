package org.zerock.b01.dto.operation;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ContractDTO {
    private String conCode; // 계약코드 [Contract 기본키]
    private Long partnerId; // 협력업체 ID [Partner 기본키]
    private List<String> materialCodes; // 자재코드 [Material 기본키]
    private List<Integer> materialPrices; // (계약) 단가
    private List<Integer> materialQtys; // 수량
    private List<Integer> materialSchedules; // 소요일 (일수)
    private List<String> materialExplains; // 합의내용
    private LocalDate startDate; // 계약일 (시작일)
    private LocalDate endDate; // 만료일 (종료일)
}
