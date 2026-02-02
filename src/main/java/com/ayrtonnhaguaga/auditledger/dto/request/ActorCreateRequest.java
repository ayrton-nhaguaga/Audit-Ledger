package com.ayrtonnhaguaga.auditledger.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ActorCreateRequest {

    @NotBlank
    @Size(max = 80)
    private String id;

    @Size(max = 120)
    private String displayName;
}