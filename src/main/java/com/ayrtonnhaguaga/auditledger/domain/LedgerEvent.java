package com.ayrtonnhaguaga.auditledger.domain;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "ledger_events",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_event_id", columnNames = "event_id"),
                @UniqueConstraint(name = "uk_stream_seq", columnNames = {"stream_id", "seq"})
        },
        indexes = {
                @Index(name = "idx_events_stream_time", columnList = "stream_id,event_time"),
                @Index(name = "idx_events_hash", columnList = "hash")
        }
)
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class LedgerEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_id", length = 36, nullable = false, updatable = false)
    private String eventId; // UUID string

    @Column(name = "stream_id", length = 80, nullable = false, updatable = false)
    private String streamId;

    @Column(name = "seq", nullable = false, updatable = false)
    private long seq;

    /**
     * timestamp do cliente (o "event time").
     * Em produção, você pode validar tolerância de clock.
     */
    @Column(name = "event_time", nullable = false, updatable = false)
    private LocalDateTime eventTime;

    @Column(name = "actor_id", length = 80, nullable = false, updatable = false)
    private String actorId;

    @Column(name = "type", length = 80, nullable = false, updatable = false)
    private String type;

    /**
     * JSON canônico (string) usado no hash.
     * Importante: não reformatar depois de salvar.
     */
    @Lob
    @Column(name = "payload_canonical", nullable = false, updatable = false, columnDefinition = "LONGTEXT")
    private String payloadCanonical;

    @Column(name = "prev_hash", length = 64, updatable = false)
    private String prevHash;

    @Column(name = "hash", length = 64, nullable = false, updatable = false)
    private String hash;

    /**
     * Assinatura Base64 do hash.
     */
    @Lob
    @Column(name = "signature_b64", nullable = false, updatable = false, columnDefinition = "TEXT")
    private String signatureB64;

    /**
     * Chave usada para assinar (key registry).
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "key_id", nullable = false, updatable = false)
    private ActorKey key;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void prePersist() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }
}