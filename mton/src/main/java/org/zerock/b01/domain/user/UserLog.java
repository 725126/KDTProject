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
public class UserLog extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userLogId;

    private String sActionType;

    private String sActionContent;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // (외래키) 회원 ID

}
