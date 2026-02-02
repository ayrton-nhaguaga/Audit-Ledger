package com.ayrtonnhaguaga.auditledger.repository;

import com.ayrtonnhaguaga.auditledger.domain.Actor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActorRepository extends JpaRepository<Actor, String> {
}