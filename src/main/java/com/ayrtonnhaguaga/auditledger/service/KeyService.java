package com.ayrtonnhaguaga.auditledger.service;

import com.ayrtonnhaguaga.auditledger.domain.Actor;
import com.ayrtonnhaguaga.auditledger.domain.ActorKey;
import com.ayrtonnhaguaga.auditledger.domain.KeyAlgorithm;
import com.ayrtonnhaguaga.auditledger.domain.KeyStatus;
import com.ayrtonnhaguaga.auditledger.dto.request.KeyRegisterRequest;
import com.ayrtonnhaguaga.auditledger.dto.response.KeyRegisterResponse;
import com.ayrtonnhaguaga.auditledger.dto.response.KeyResponse;
import com.ayrtonnhaguaga.auditledger.exception.ApiException;

import com.ayrtonnhaguaga.auditledger.repository.ActorKeyRepository;
import com.ayrtonnhaguaga.auditledger.repository.ActorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

@Service
@RequiredArgsConstructor
public class KeyService {

    private final ActorRepository actorRepository;
    private final ActorKeyRepository actorKeyRepository;

    @Transactional
    public KeyRegisterResponse registerKey(String actorId, KeyRegisterRequest req) {
        Actor actor = actorRepository.findById(actorId)
                .orElseThrow(() -> ApiException.notFound("Actor não encontrado"));

        if (!actor.isActive()) {
            throw ApiException.conflict("Actor está inativo");
        }

        // valida base64 + tamanho esperado (raw 32 bytes)
        byte[] pkBytes;
        try {
            pkBytes = Base64.getDecoder().decode(req.getPublicKeyB64());
        } catch (Exception e) {
            throw ApiException.badRequest("publicKeyB64 inválida (Base64)");
        }
        if (pkBytes.length != 32) {
            throw ApiException.badRequest("publicKeyB64 deve ser Ed25519 raw (32 bytes) em Base64");
        }

        ActorKey key = ActorKey.builder()
                .actor(actor)
                .algorithm(KeyAlgorithm.ED25519)
                .status(KeyStatus.ACTIVE)
                .publicKeyB64(req.getPublicKeyB64())
                .createdAt(LocalDateTime.now())
                .build();

        ActorKey saved = actorKeyRepository.save(key);

        return KeyRegisterResponse.builder()
                .keyId(saved.getId())
                .actorId(actorId)
                .algorithm(saved.getAlgorithm().name())
                .status(saved.getStatus().name())
                .createdAt(saved.getCreatedAt())
                .build();
    }

    @Transactional
    public void revokeKey(String actorId, Long keyId) {
        ActorKey key = actorKeyRepository.findById(keyId)
                .orElseThrow(() -> ApiException.notFound("Key não encontrada"));

        if (!key.getActor().getId().equals(actorId)) {
            throw ApiException.badRequest("Key não pertence ao actor");
        }
        if (key.getStatus() == KeyStatus.REVOKED) return;

        key.setStatus(KeyStatus.REVOKED);
        key.setRevokedAt(LocalDateTime.now());
        actorKeyRepository.save(key);
    }

    @Transactional(readOnly = true)
    public List<KeyResponse> listKeys(String actorId) {
        return actorKeyRepository.findAllByActor_IdOrderByIdDesc(actorId)
                .stream()
                .map(k -> KeyResponse.builder()
                        .keyId(k.getId())
                        .algorithm(k.getAlgorithm().name())
                        .status(k.getStatus().name())
                        .createdAt(k.getCreatedAt())
                        .revokedAt(k.getRevokedAt())
                        .build())
                .toList();
    }
}