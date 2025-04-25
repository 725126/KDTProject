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
public class Material {
    @Id
    private String matId;

    @Column(length = 120, nullable = false)
    private String matName;

    @Column(length = 20, nullable = false)
    private String matType;

    @Column(length = 20, nullable = false)
    private String matMeasure;

    @Column(length = 8, nullable = false)
    private String matUnit;

    @Column(length = 200)
    private String matExplain;
}
