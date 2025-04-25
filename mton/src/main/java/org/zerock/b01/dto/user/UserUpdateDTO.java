package org.zerock.b01.dto.user;

import lombok.*;
import org.zerock.b01.domain.user.UserRole;
import org.zerock.b01.domain.user.UserStatus;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserUpdateDTO { // 회원정보 수정(공통)
    private String uName;
    private String uPhone;
    private UserStatus uIsActive;
    private UserRole userRole;
}
