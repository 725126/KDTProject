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
public class UserUpdateDTO { // 회원정보 수정(공통)
    private String name;
    private String email;
    private String phone;

    // 회원종류 구분 위해 사용
    private UserRole userRole;

    // PARTNER일 경우에만 사용
    private String companyName;
    private String address;
}
