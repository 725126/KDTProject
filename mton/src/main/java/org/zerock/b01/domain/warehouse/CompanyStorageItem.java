package org.zerock.b01.domain.warehouse;

import jakarta.persistence.*;
import lombok.*;
import org.zerock.b01.domain.operation.Material;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"companyStorage", "material"})
public class CompanyStorageItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long cstorageItemId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CompanyStorageStatus companyStorageStatus; // 입고/출고 등

    @Column(nullable = false)
    private int cstorageQty; // 보관 수량

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cstorage_id", nullable = false)
    private CompanyStorage companyStorage; // 창고 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mat_id", nullable = false)
    private Material material; // 자재
}
