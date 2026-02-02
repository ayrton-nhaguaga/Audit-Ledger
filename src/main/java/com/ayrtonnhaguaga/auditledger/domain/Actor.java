package com.ayrtonnhaguaga.auditledger.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "actors",
        indexes = {
                @Index(name = "idx_actors_active", columnList = "active")
        }
)
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Actor {

    @Id
    @Column(name = "id", length = 80, nullable = false, updatable = false)
    private String id;

    @Column(name = "display_name", length = 120)
    private String displayName;

    @Column(name = "active", nullable = false)
    private boolean active = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void prePersist() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }
}