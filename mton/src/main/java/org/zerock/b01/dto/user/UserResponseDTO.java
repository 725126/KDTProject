package org.zerock.b01.dto.user;

import lombok.*;
import org.zerock.b01.domain.user.UserRole;
import org.zerock.b01.domain.user.UserStatus;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class UserResponseDTO { // 회원 조회 (내부)
    private Long userId;
    private String uEmail;
    private String uName;
    private String uPhone;
    private UserRole userRole;
    private UserStatus uIsActive;

    private PartnerResponseDTO partnerResponse;
}
