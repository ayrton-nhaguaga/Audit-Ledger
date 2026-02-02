package com.ayrtonnhaguaga.auditledger.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "stream_heads")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class StreamHead {

    @Id
    @Column(name = "stream_id", length = 80, nullable = false, updatable = false)
    private String streamId;

    @Column(name = "last_seq", nullable = false)
    private long lastSeq;

    @Column(name = "last_hash", length = 64)
    private String lastHash; // pode ser null se stream vazio

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    void prePersist() {
        if (updatedAt == null) updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}