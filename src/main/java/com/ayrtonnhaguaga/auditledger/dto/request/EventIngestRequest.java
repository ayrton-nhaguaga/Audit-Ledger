package com.ayrtonnhaguaga.auditledger.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class EventIngestRequest {

    @NotBlank
    @Size(max = 36)
    private String eventId; // UUID string

    @NotBlank
    @Size(max = 80)
    private String streamId;

    /**
     * ISO-8601
     */
    @NotBlank
    private String timestamp;

    @NotBlank
    @Size(max = 80)
    private String actorId;

    @NotNull
    private Long keyId;

    @NotBlank
    @Size(max = 80)
    private String type;


    @NotBlank
    private String payloadCanonical;

    /**
     * Para primeiro evento do stream: pode ser null ou "".
     */
    private String prevHash;

    @NotBlank
    @Size(min = 64, max = 64)
    private String hash; // sha-256 hex

    @NotBlank
    private String signatureB64; // assinatura do hash (Base64)
}
