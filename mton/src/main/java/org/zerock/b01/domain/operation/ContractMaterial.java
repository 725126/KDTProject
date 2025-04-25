package org.zerock.b01.domain.operation;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ContractMaterial {
    @Id
    private String cmtId;

    @ManyToOne
    @JoinColumn(name = "con_id", nullable = false)
    private Contract contract;

    @ManyToOne
    @JoinColumn(name = "mat_id", nullable = false)
    private Material material;

    @Column(nullable = false)
    private int cmtPrice;

    @Column(nullable = false)
    private int cmtReq;
}
