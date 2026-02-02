package com.ayrtonnhaguaga.auditledger.dto.response;


import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class EventResponse {
    private String eventId;
    private String streamId;
    private long seq;
    private LocalDateTime eventTime;
    private String actorId;
    private Long keyId;
    private String type;
    private String payloadCanonical;
    private String prevHash;
    private String hash;
    private String signatureB64;
}