package org.zerock.b01.domain.operation;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ContractMaterial {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String cmtId;
}
