package org.urban.alert.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.urban.alert.entity.InterventionUpdate;

import java.util.List;

@Repository
public interface InterventionUpdateRepository extends JpaRepository<InterventionUpdate, Long> {

    List<InterventionUpdate> findByInterventionIdOrderByCreatedAtAsc(Long interventionId);
}
