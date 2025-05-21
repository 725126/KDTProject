package org.zerock.b01.domain.operation;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Check;

import java.time.LocalDate;

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

    @Column(nullable = false, updatable = false)
    private LocalDate pplanDate;

    @Column(nullable = false)
    private LocalDate pplanEnd;

    @Column(name = "pplan_stat", nullable = false)
    @Check(constraints = "pplan_stat IN ('진행중', '완료', '취소')")
    private String pplanStat;

    public void changeStat(String stat) {
        this.pplanStat = stat;
    }
}
