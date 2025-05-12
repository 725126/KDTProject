package org.zerock.b01.domain.operation;

import jakarta.persistence.*;
import lombok.*;
import org.zerock.b01.domain.user.Partner;

import java.time.LocalDate;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Contract {
    @Id
    private String conId;

    @ManyToOne
    @JoinColumn(name = "partner_id", nullable = false)
    private Partner partner;

    @Column(nullable = false)
    private LocalDate conDate;

    @Column(nullable = false)
    private LocalDate conEnd;

    @Column(length = 800, nullable = false)
    private String conContent;
}
