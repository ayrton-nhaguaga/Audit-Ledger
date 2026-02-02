package com.ayrtonnhaguaga.auditledger.dto.response;


import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ActorResponse {
    private String id;
    private String displayName;
    private boolean active;
    private LocalDateTime createdAt;
}