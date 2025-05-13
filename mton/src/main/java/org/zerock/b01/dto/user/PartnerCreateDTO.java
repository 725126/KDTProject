package org.zerock.b01.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class PartnerCreateDTO { // 회원 가입 (외부)
    // Partner 테이블에 들어가는 추가 정보
    @NotBlank(message = "회사명은 필수 입력입니다.")
    @Size(min = 2, max = 50, message = "회사명은 2자 이상 50자 이하로 입력해야 합니다.")
    private String pCompany;

    @NotBlank(message = "사업자등록번호는 필수 입력입니다.")
    @Pattern(
            regexp = "^\\d{3}-\\d{2}-\\d{5}$",
            message = "사업자등록번호는 000-00-00000 형식이어야 합니다."
    )
    private String pBusinessNo;

    @NotBlank(message = "주소는 필수 입력입니다.")
    @Size(max = 100, message = "주소는 100자 이내로 입력해야 합니다.")
    private String pAddr;
}
