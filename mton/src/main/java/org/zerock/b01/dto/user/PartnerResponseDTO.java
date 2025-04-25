package org.zerock.b01.dto.user;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PartnerResponseDTO { // 회원 조회 (외부)
    private Long partnerId;
    private String pCompany;
    private String pAddr;
    private String pBusinessNo;

    private UserResponseDTO user;
}
