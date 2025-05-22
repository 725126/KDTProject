package org.zerock.b01.dto.warehouse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CompanyStorageDTO {

  private String cstorageId;

  private String cstorageAddress;

  private String cstorageContactNumber;

  private String cstorageManager;
}
