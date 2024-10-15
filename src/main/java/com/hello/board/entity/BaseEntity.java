package com.hello.board.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
@Getter
public class BaseEntity {   //게시물 작성 시간, 수정 시간을 다루는 엔티티 클래스
    @CreationTimestamp  //생성 시 시간을 준다.
    @Column(updatable = false)  //update에는 동작 x
    private LocalDateTime createdTime;

    @UpdateTimestamp    //수정 시 시간을 준다.
    @Column(insertable = false) //insert에는 동작 x
    private LocalDateTime updatedTime;
}

