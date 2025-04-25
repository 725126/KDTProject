package org.zerock.b01.dto.user;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PartnerUpdateDTO {
    private String pCompany;
    private String pAddr;
    private String pBusinessNo;
}
