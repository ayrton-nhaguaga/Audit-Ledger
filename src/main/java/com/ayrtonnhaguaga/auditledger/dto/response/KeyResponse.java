package com.ayrtonnhaguaga.auditledger.dto.response;


import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class KeyResponse {
    private Long keyId;
    private String algorithm;   // "ED25519"
    private String status;      // "ACTIVE" / "REVOKED"
    private LocalDateTime createdAt;
    private LocalDateTime revokedAt;
}