package com.ayrtonnhaguaga.auditledger.dto.response;


import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class KeyRegisterResponse {
    private Long keyId;
    private String actorId;
    private String algorithm; // "ED25519"
    private String status;    // "ACTIVE"
    private LocalDateTime createdAt;
}