package org.zerock.b01.dto.warehouse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PartnerStorageDTO {

  private Long pstorageId;

  private String matId;

  private String matName;

  private int sstorageQty;

  private Long partnerId;
}
