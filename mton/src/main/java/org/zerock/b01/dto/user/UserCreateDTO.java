package org.zerock.b01.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.zerock.b01.domain.user.UserRole;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class UserCreateDTO { // 회원 가입 (기본)
    @NotBlank(message = "이메일은 필수 입력입니다.")
    @Email(message = "유효한 이메일 형식이어야 합니다.")
    private String uEmail;

    @NotBlank(message = "비밀번호는 필수 입력입니다.")
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{5,16}$",
            message = "비밀번호는 영문, 숫자 포함 5자리 이상이어야 합니다."
    )
    private String uPassword;

    @NotBlank(message = "비밀번호 확인은 필수 입력입니다.")
    private String uPasswordConfirm;

    @NotBlank(message = "이름은 필수 입력입니다.")
    @Pattern(
            regexp = "^[a-zA-Z가-힣]{2,20}$",
            message = "이름은 영문 또는 한글로 2자 이상 20자 이하여야 합니다."
    )
    private String uName;

    @NotBlank(message = "연락처는 필수 입력입니다.")
    @Pattern(
            regexp = "^010-\\d{4}-\\d{4}$",
            message = "연락처는 010-0000-0000 형식이어야 합니다."
    )
    private String uPhone;

    private UserRole userRole;

    private PartnerCreateDTO partnerCreateDTO;
}
