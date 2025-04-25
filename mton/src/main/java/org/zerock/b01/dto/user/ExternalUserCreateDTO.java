package org.zerock.b01.dto.user;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExternalUserCreateDTO { // 회원 가입 (외부)
    // User 테이블에 들어가는 기본 회원 정보
    private String uEmail;
    private String uPassword;
    private String uName;
    private String uPhone;

    // Partner 테이블에 들어가는 추가 정보
    private String pCompany;
    private String pAddr;
    private String pBusinessNo;
}
