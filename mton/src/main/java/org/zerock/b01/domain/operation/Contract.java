package org.zerock.b01.domain.operation;

import jakarta.persistence.*;
import lombok.*;
import org.zerock.b01.domain.user.Partner;

import java.util.Date;

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
    private Date conDate;

    @Column(nullable = false)
    private Date conEnd;

    @Column(length = 800, nullable = false)
    private String conContent;
}
