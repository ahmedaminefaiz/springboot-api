package org.urban.alert.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.urban.alert.entity.Problem;
import org.urban.alert.entity.enums.ProblemStatusEnum;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProblemRepository extends JpaRepository<Problem, Long> {

    // Trouver les problèmes créés par un SuperAgent
    Page<Problem> findByUserId(Long userId, Pageable pageable);

    // Trouver les problèmes assignés à un Agent
    Page<Problem> findByAssignedToId(Long assignedToId, Pageable pageable);

    // Trouver les problèmes par statut
    Page<Problem> findByStatus(ProblemStatusEnum status, Pageable pageable);

    // Trouver les problèmes non résolus
    @Query("SELECT p FROM Problem p WHERE p.status != 'RESOLVED' AND p.status != 'REJECTED'")
    List<Problem> findActiveProblems();

    // Trouver les problèmes d'un agent par statut
    @Query("SELECT p FROM Problem p WHERE p.assignedTo.id = :agentId AND p.status = :status")
    List<Problem> findByAgentAndStatus(@Param("agentId") Long agentId, 
                                        @Param("status") ProblemStatusEnum status);

    // Compter les problèmes par statut
    Long countByStatus(ProblemStatusEnum status);

    // Compter les problèmes assignés à un agent
    Long countByAssignedToId(Long assignedToId);

    // Trouver les problèmes créés par un utilisateur
    Long countByUserId(Long userId);

    // Trouver les problèmes contenant au moins une alerte créée par un utilisateur donné
    @Query("SELECT DISTINCT p FROM Problem p JOIN p.alerts a WHERE a.user.id = :userId")
    Page<Problem> findByAlertCreatorId(@Param("userId") Long userId, Pageable pageable);
}