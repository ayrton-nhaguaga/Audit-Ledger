package com.ayrtonnhaguaga.auditledger.dto.response;


import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class EventIngestResponse {
    private String eventId;
    private String streamId;
    private long seq;
    private String hash;
    private String prevHash;
    private LocalDateTime storedAt;
}