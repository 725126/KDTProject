package org.zerock.b01.domain.operation;

import jakarta.persistence.*;
import lombok.*;
import org.zerock.b01.domain.user.Partner;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "items")
public class Transaction {
    @Id
    private String tranId; // 거래명세서 ID (예: TR20240519-A1B2C)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partner_id", nullable = false)
    private Partner partner; // 협력업체 정보

    @Column(nullable = false)
    private LocalDate tranDate; // 발행일자

    @OneToMany(mappedBy = "transaction", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TransactionItem> items = new ArrayList<>();

    public void addItem(TransactionItem item) {
        items.add(item);
        item.setTransaction(this);
    }
}
