package org.zerock.b01.dto.user;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PartnerUpdateDTO {
    @NotEmpty(message = "사업자등록번호는 필수입니다.")
    private String pBusinessNo;

    private String pAddr;

    private UserUpdateDTO userUpdateDTO;
}
