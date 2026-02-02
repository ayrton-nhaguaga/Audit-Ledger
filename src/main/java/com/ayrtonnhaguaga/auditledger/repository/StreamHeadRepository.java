package com.ayrtonnhaguaga.auditledger.repository;


import com.ayrtonnhaguaga.auditledger.domain.StreamHead;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import java.util.Optional;

public interface StreamHeadRepository extends JpaRepository<StreamHead, String> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select sh from StreamHead sh where sh.streamId = :streamId")
    Optional<StreamHead> findForUpdate(@Param("streamId") String streamId);
}