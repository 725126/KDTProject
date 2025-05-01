package org.zerock.b01.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class FindIdDTO {
    @JsonProperty("uName")
    private String uName;
    @JsonProperty("uPhone")
    private String uPhone;
    @JsonProperty("userRole")
    private String userRole;
    @JsonProperty("pBusinessNo")
    private String pBusinessNo;
}
