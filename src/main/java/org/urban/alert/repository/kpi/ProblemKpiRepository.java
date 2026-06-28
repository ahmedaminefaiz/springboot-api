package org.urban.alert.repository.kpi;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.urban.alert.entity.Problem;

import java.time.LocalDateTime;

@Repository
public interface ProblemKpiRepository extends JpaRepository<Problem, Long> {

    @Query("""
            SELECT COUNT(p) FROM Problem p
            WHERE p.createdAt >= :since
            AND (:criticalityId = -1 OR p.criticality.id = :criticalityId)
            AND (:problemTypeId = -1 OR EXISTS (
                SELECT a FROM Alert a WHERE a.problem = p AND a.category.id = :problemTypeId))
            """)
    Long countTotal(@Param("since") LocalDateTime since,
                    @Param("criticalityId") long criticalityId,
                    @Param("problemTypeId") long problemTypeId);

    @Query("""
            SELECT COUNT(p) FROM Problem p
            WHERE p.status NOT IN ('RESOLVED', 'REJECTED')
            AND p.createdAt >= :since
            AND (:criticalityId = -1 OR p.criticality.id = :criticalityId)
            AND (:problemTypeId = -1 OR EXISTS (
                SELECT a FROM Alert a WHERE a.problem = p AND a.category.id = :problemTypeId))
            """)
    Long countOuverts(@Param("since") LocalDateTime since,
                      @Param("criticalityId") long criticalityId,
                      @Param("problemTypeId") long problemTypeId);

    @Query("""
            SELECT COUNT(p) FROM Problem p
            WHERE p.status = 'RESOLVED'
            AND p.createdAt >= :since
            AND (:criticalityId = -1 OR p.criticality.id = :criticalityId)
            AND (:problemTypeId = -1 OR EXISTS (
                SELECT a FROM Alert a WHERE a.problem = p AND a.category.id = :problemTypeId))
            """)
    Long countResolus(@Param("since") LocalDateTime since,
                      @Param("criticalityId") long criticalityId,
                      @Param("problemTypeId") long problemTypeId);

    @Query("""
            SELECT COUNT(p) FROM Problem p
            WHERE p.status NOT IN ('RESOLVED', 'REJECTED')
            AND NOT EXISTS (
                SELECT i FROM Intervention i
                WHERE i.problem = p AND i.status IN ('AFFECTEE', 'EN_COURS'))
            AND p.createdAt >= :since
            AND (:criticalityId = -1 OR p.criticality.id = :criticalityId)
            AND (:problemTypeId = -1 OR EXISTS (
                SELECT a FROM Alert a WHERE a.problem = p AND a.category.id = :problemTypeId))
            """)
    Long countSansInterventionActive(@Param("since") LocalDateTime since,
                                     @Param("criticalityId") long criticalityId,
                                     @Param("problemTypeId") long problemTypeId);

    @Query(value = """
            SELECT AVG(EXTRACT(EPOCH FROM (p.resolved_at - p.created_at)) / 3600.0)
            FROM problems p
            WHERE p.resolved_at IS NOT NULL
            AND p.created_at >= :since
            AND (:criticalityId = -1 OR p.criticality_id = :criticalityId)
            AND (:problemTypeId = -1 OR EXISTS (
                SELECT 1 FROM alerts a WHERE a.problem_id = p.id AND a.category_id = :problemTypeId))
            """, nativeQuery = true)
    Double avgTempsMoyenResolutionHeures(@Param("since") LocalDateTime since,
                                         @Param("criticalityId") long criticalityId,
                                         @Param("problemTypeId") long problemTypeId);
}