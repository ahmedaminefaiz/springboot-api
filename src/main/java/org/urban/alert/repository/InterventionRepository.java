package org.urban.alert.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.urban.alert.entity.Intervention;

@Repository
public interface InterventionRepository extends JpaRepository<Intervention, Long> {

    Page<Intervention> findByAgentId(Long agentId, Pageable pageable);

    Page<Intervention> findByProblemId(Long problemId, Pageable pageable);

    boolean existsByProblemIdAndAgentId(Long problemId, Long agentId);
}
