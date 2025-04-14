package org.zerock.b01.domain;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@MappedSuperclass
@EntityListeners(value = { AuditingEntityListener.class })
@Getter
abstract class BaseEntity {

    // 생성일자
    @CreatedDate
    @Column(name = "cre_date", updatable = false)
    private LocalDateTime creDate; // 생성 시각

    // 수정일자
    @LastModifiedDate
    @Column(name ="mod_date" )
    private LocalDateTime modDate; // 수정 시각

}
