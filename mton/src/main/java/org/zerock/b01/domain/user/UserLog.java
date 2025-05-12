package org.zerock.b01.domain.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.zerock.b01.domain.BaseEntity;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserLog extends BaseEntity { // 회원 로그 (활동 기록용)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userLogId; // 회원 로그 ID

    private String sActionType; // 활동 타입 (DELETE / CREATE / UPDATE)

    private String sActionContent; // 활동 내용 ([상세 활동명] 활동 내용)

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // (외래키) 회원 ID

}
