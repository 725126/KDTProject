package org.zerock.b01.domain.operation;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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
    @JoinColumn(name = "mat_id", nullable = true) // nullable = false > nullable = true
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private Material material;

    @Column(nullable = false)
    private int cmtPrice;

    @Column(nullable = false)
    private int cmtReq;
}
