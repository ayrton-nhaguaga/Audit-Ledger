package com.ayrtonnhaguaga.auditledger.repository;

import com.ayrtonnhaguaga.auditledger.domain.LedgerEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LedgerEventRepository extends JpaRepository<LedgerEvent, Long> {

    Optional<LedgerEvent> findByEventId(String eventId);

    List<LedgerEvent> findAllByStreamIdOrderBySeqAsc(String streamId);

    Optional<LedgerEvent> findTopByStreamIdOrderBySeqDesc(String streamId);
}
