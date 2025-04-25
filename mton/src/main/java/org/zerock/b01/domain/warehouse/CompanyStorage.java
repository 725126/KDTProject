package org.zerock.b01.domain.warehouse;

import jakarta.persistence.*;
import lombok.*;
import org.zerock.b01.domain.BaseEntity;
import org.zerock.b01.domain.operation.Material;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CompanyStorage extends BaseEntity {

  @Id
  private String cstorageId;

  @ManyToOne
  @JoinColumn(nullable = false)
  private Material material;

  @Column(length = 150)
  private String cstorageAddress;

  @Column(length = 20)
  private String cstorageContactNumber;

  @Column(length = 20)
  private String cstorageManager;

  @Column(nullable = false)
  private int cstorageQty;
}
