package com.ayrtonnhaguaga.auditledger.util;

import com.ayrtonnhaguaga.auditledger.dto.request.EventIngestRequest;


public final class HashBuilder {
    private HashBuilder() {}


    //TODO:Não mude isso depois de ter dados gravados!

    public static String buildCoreString(EventIngestRequest r) {
        // separador fixo para evitar ambiguidades
        // payloadCanonical já é string JSON determinística
        return String.join("|",
                nullSafe(r.getEventId()),
                nullSafe(r.getStreamId()),
                nullSafe(r.getTimestamp()),
                nullSafe(r.getActorId()),
                String.valueOf(r.getKeyId()),
                nullSafe(r.getType()),
                nullSafe(r.getPayloadCanonical()),
                nullSafe(r.getPrevHash())
        );
    }

    private static String nullSafe(String s) {
        return s == null ? "" : s;
    }
}