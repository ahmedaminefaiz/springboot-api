package org.urban.alert.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.urban.alert.entity.Criticality;

import java.util.Optional;

public interface CriticalityRepository extends JpaRepository<Criticality, Long> {
    Optional<Criticality> findByName(String name);
    boolean existsByName(String name);
}