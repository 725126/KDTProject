package org.zerock.b01.domain.operation;

import jakarta.persistence.*;
import lombok.*;
import org.zerock.b01.domain.BaseEntity;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ContractFile extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long fileId; // 기본키

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "con_id", nullable = false)
    private Contract contract; // 계약정보 연관관계

    @Column(nullable = false, length = 255)
    private String originalFileName; // 업로드한 원본 파일명

    @Column(nullable = false, length = 255)
    private String savedFileName; // 서버에 저장된 파일명 (UUID 등)

    @Column(nullable = false)
    private String filePath; // 파일 저장 경로
}
