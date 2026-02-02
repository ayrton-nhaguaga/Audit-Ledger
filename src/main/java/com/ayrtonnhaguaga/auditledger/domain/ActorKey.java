package com.ayrtonnhaguaga.auditledger.domain;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "actor_keys",
        indexes = {
                @Index(name = "idx_actor_keys_actor_status", columnList = "actor_id,status"),
                @Index(name = "idx_actor_keys_status", columnList = "status")
        }
)
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class ActorKey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // keyId (pode expor como string no DTO)

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "actor_id", nullable = false)
    private Actor actor;

    @Enumerated(EnumType.STRING)
    @Column(name = "algorithm", length = 20, nullable = false)
    private KeyAlgorithm algorithm;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private KeyStatus status = KeyStatus.ACTIVE;

    /**
     * Salve a publicKey em Base64 (MVP).
     * Em produção, prefira VARBINARY e/ou formato padronizado.
     */
    @Lob
    @Column(name = "public_key_b64", nullable = false, columnDefinition = "TEXT")
    private String publicKeyB64;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "revoked_at")
    private LocalDateTime revokedAt;

    @PrePersist
    void prePersist() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (status == null) status = KeyStatus.ACTIVE;
    }
}