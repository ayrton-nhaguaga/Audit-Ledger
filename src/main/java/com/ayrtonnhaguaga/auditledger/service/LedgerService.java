package com.ayrtonnhaguaga.auditledger.service;


import com.ayrtonnhaguaga.auditledger.crypto.CryptoService;
import com.ayrtonnhaguaga.auditledger.domain.*;
import com.ayrtonnhaguaga.auditledger.dto.request.EventIngestRequest;
import com.ayrtonnhaguaga.auditledger.dto.response.EventIngestResponse;
import com.ayrtonnhaguaga.auditledger.exception.ApiException;
import com.ayrtonnhaguaga.auditledger.repository.ActorKeyRepository;
import com.ayrtonnhaguaga.auditledger.repository.LedgerEventRepository;
import com.ayrtonnhaguaga.auditledger.repository.StreamHeadRepository;
import com.ayrtonnhaguaga.auditledger.util.HashBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;

@Service
@RequiredArgsConstructor
public class LedgerService {

    private final CryptoService cryptoService;
    private final ActorKeyRepository actorKeyRepository;
    private final StreamHeadRepository streamHeadRepository;
    private final LedgerEventRepository ledgerEventRepository;

    @Transactional
    public EventIngestResponse ingest(EventIngestRequest req) {
        // 0 -  anti-replay
        if (ledgerEventRepository.findByEventId(req.getEventId()).isPresent()) {
            throw ApiException.conflict("eventId já existe");
        }

        // 1 - buscar chave (ativa)
        ActorKey key = actorKeyRepository
                .findByIdAndActor_IdAndStatus(req.getKeyId(), req.getActorId(), KeyStatus.ACTIVE)
                .orElseThrow(() -> ApiException.notFound("Key ativa não encontrada para esse actorId/keyId"));

        if (key.getAlgorithm() != KeyAlgorithm.ED25519) {
            throw ApiException.badRequest("Key não é ED25519");
        }

        // 2 - lock stream head
        StreamHead head = streamHeadRepository.findForUpdate(req.getStreamId())
                .orElseGet(() -> StreamHead.builder()
                        .streamId(req.getStreamId())
                        .lastSeq(0L)
                        .lastHash(null)
                        .build());

        // 3 - validar encadeamento prevHash
        String expectedPrev = head.getLastHash();
        String providedPrev = normalizePrev(req.getPrevHash());

        if (expectedPrev == null) {
            // primeiro evento do stream
            if (providedPrev != null) {
                throw ApiException.conflict("prevHash deve ser vazio/null no primeiro evento do stream");
            }
        } else {
            if (providedPrev == null || !expectedPrev.equals(providedPrev)) {
                throw ApiException.conflict("prevHash não bate com o último hash do stream");
            }
        }

        // 4 - recalcular hash do core
        String core = HashBuilder.buildCoreString(req);
        String computedHash = cryptoService.sha256Hex(core);

        if (!computedHash.equalsIgnoreCase(req.getHash())) {
            throw ApiException.badRequest("Hash inválido (conteúdo não confere)");
        }

        // 5 - verificar assinatura do hash (mensagem = hash hex)
        cryptoService.verifyEd25519(key.getPublicKeyB64(), req.getSignatureB64(), req.getHash());

        // 6 - calcular seq e salvar
        long nextSeq = head.getLastSeq() + 1;

        LedgerEvent event = LedgerEvent.builder()
                .eventId(req.getEventId())
                .streamId(req.getStreamId())
                .seq(nextSeq)
                .eventTime(parseIsoToLocalDateTimeUTC(req.getTimestamp()))
                .actorId(req.getActorId())
                .type(req.getType())
                .payloadCanonical(req.getPayloadCanonical())
                .prevHash(expectedPrev) // o prev real
                .hash(req.getHash().toLowerCase())
                .signatureB64(req.getSignatureB64())
                .key(key)
                .build();

        ledgerEventRepository.save(event);

        // 7 - atualizar head (se novo stream, precisa persistir head antes)
        head.setLastSeq(nextSeq);
        head.setLastHash(event.getHash());
        streamHeadRepository.save(head);

        return EventIngestResponse.builder()
                .eventId(event.getEventId())
                .streamId(event.getStreamId())
                .seq(event.getSeq())
                .hash(event.getHash())
                .prevHash(event.getPrevHash())
                .storedAt(event.getCreatedAt())
                .build();
    }

    private String normalizePrev(String prev) {
        if (prev == null) return null;
        String p = prev.trim();
        return p.isEmpty() ? null : p;
    }

    private LocalDateTime parseIsoToLocalDateTimeUTC(String iso) {
        try {
            // aceita "Z" e offsets
            Instant instant = Instant.parse(iso);
            return LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
        } catch (Exception e) {
            // fallback: se vier sem Z/offset, tenta parsear local
            try {
                return LocalDateTime.parse(iso);
            } catch (Exception ex) {
                throw ApiException.badRequest("timestamp inválido. Use ISO-8601 com Z. Ex: 2026-01-27T13:05:00Z");
            }
        }
    }
}