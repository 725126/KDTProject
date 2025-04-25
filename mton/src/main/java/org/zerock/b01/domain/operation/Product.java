package org.zerock.b01.domain.operation;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Product {
    @Id
    private String prodId;

    @Column(length = 120, nullable = false)
    private String prodName;

    @Column(length = 20, nullable = false)
    private String prodMeasure;

    @Column(length = 20, nullable = false)
    private String prodUnit;

    @Column(length = 20, nullable = false)
    private String prodExplain;
}
