package org.urban.alert.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.urban.alert.entity.ProblemStatusHistory;
import org.urban.alert.entity.enums.ProblemStatusEnum;

import java.util.List;

@Repository
public interface ProblemStatusHistoryRepository extends JpaRepository<ProblemStatusHistory, Long> {

    // Trouver l'historique complet d'un problème
    @Query("SELECT h FROM ProblemStatusHistory h WHERE h.problem.id = :problemId ORDER BY h.changedAt DESC")
    List<ProblemStatusHistory> findByProblemId(@Param("problemId") Long problemId);

    // Trouver l'historique paginé d'un problème
    Page<ProblemStatusHistory> findByProblemIdOrderByChangedAtDesc(Long problemId, Pageable pageable);

    // Trouver les changements de statut spécifiques
    @Query("SELECT h FROM ProblemStatusHistory h WHERE h.problem.id = :problemId AND h.newStatus = :status")
    List<ProblemStatusHistory> findByProblemIdAndStatus(@Param("problemId") Long problemId, 
                                                         @Param("status") ProblemStatusEnum status);

    // Trouver les derniers changements d'un statut
    @Query("SELECT h FROM ProblemStatusHistory h WHERE h.newStatus = :status ORDER BY h.changedAt DESC")
    List<ProblemStatusHistory> findRecentStatusChanges(@Param("status") ProblemStatusEnum status, 
                                                        Pageable pageable);

    // Compter les changements par statut final
    Long countByNewStatus(ProblemStatusEnum status);
}