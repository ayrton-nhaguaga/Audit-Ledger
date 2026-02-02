package com.ayrtonnhaguaga.auditledger.repository;

import com.ayrtonnhaguaga.auditledger.domain.ActorKey;
import com.ayrtonnhaguaga.auditledger.domain.KeyStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ActorKeyRepository extends JpaRepository<ActorKey, Long> {

    Optional<ActorKey> findByIdAndActor_IdAndStatus(Long id, String actorId, KeyStatus status);

    List<ActorKey> findAllByActor_IdOrderByIdDesc(String actorId);

    List<ActorKey> findAllByActor_IdAndStatusOrderByIdDesc(String actorId, KeyStatus status);
}