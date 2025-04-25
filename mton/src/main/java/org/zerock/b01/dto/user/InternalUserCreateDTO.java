package org.zerock.b01.dto.user;

import lombok.*;
import org.zerock.b01.domain.user.UserRole;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InternalUserCreateDTO { // 회원 가입 (내부)
    private String uEmail;
    private String uPassword;
    private String uName;
    private String uPhone;

    private UserRole userRole;
}
