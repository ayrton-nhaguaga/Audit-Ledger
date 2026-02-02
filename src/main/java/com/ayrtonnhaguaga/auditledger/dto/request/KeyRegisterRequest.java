package com.ayrtonnhaguaga.auditledger.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class KeyRegisterRequest {

    /**
     * Public key do Ed25519 em Base64 (raw 32 bytes).
     */
    @NotBlank
    @Size(min = 40, max = 2000)
    private String publicKeyB64;
}