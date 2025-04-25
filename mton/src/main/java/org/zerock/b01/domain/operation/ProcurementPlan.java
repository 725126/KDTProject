package org.zerock.b01.domain.operation;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Check;

import java.util.Date;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ProcurementPlan {
    @Id
    private String pplanId;

    @ManyToOne
    @JoinColumn(name = "prdplan_id", nullable = false)
    private ProductionPlan prdplan;

    @ManyToOne
    @JoinColumn(name = "mat_id", nullable = false)
    private Material material;

    @Column(nullable = false)
    private int ppmatQty;

    @Column(nullable = false)
    private Date pplanDate;

    @Column(nullable = false)
    private Date pplanEnd;

    @Column(name = "pplan_stat", nullable = false)
    @Check(constraints = "pplan_stat IN ('진행중', '완료')")
    private String pplanStat;
}
