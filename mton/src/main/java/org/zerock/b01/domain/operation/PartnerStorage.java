package org.zerock.b01.domain.operation;

import jakarta.persistence.*;
import lombok.*;
import org.zerock.b01.domain.user.Partner;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PartnerStorage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pstorageId;

    @ManyToOne
    @JoinColumn(name = "partner_id", nullable = false)
    private Partner partner;

    @ManyToOne
    @JoinColumn(name = "mat_id", nullable = false)
    private Material material;

    @Column(nullable = false)
    private int sstorageQty;

    public void changeStorageQty(int newQty) {
        if (newQty < 0) throw new IllegalArgumentException("재고는 음수가 될 수 없습니다.");
        this.sstorageQty = newQty;
    }
}
