package org.urban.alert.repository.kpi;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.urban.alert.entity.Intervention;
import org.urban.alert.entity.enums.InterventionStatusEnum;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface InterventionKpiRepository extends JpaRepository<Intervention, Long> {

    @Query("""
            SELECT COUNT(i) FROM Intervention i
            WHERE i.interventionDate >= :since
            AND (:agentId = -1 OR i.agent.id = :agentId)
            AND (:criticalityId = -1 OR i.problem.criticality.id = :criticalityId)
            """)
    Long countTotal(@Param("since") LocalDateTime since,
                    @Param("agentId") long agentId,
                    @Param("criticalityId") long criticalityId);

    @Query("""
            SELECT i.status, COUNT(i) FROM Intervention i
            WHERE i.interventionDate >= :since
            AND (:agentId = -1 OR i.agent.id = :agentId)
            AND (:criticalityId = -1 OR i.problem.criticality.id = :criticalityId)
            GROUP BY i.status
            """)
    List<Object[]> countParStatut(@Param("since") LocalDateTime since,
                                  @Param("agentId") long agentId,
                                  @Param("criticalityId") long criticalityId);

    @Query("""
            SELECT COUNT(i) FROM Intervention i
            WHERE i.status IN :termineesStatuses
            AND i.interventionDate >= :since
            AND (:agentId = -1 OR i.agent.id = :agentId)
            AND (:criticalityId = -1 OR i.problem.criticality.id = :criticalityId)
            """)
    Long countTerminees(@Param("since") LocalDateTime since,
                        @Param("agentId") long agentId,
                        @Param("criticalityId") long criticalityId,
                        @Param("termineesStatuses") List<InterventionStatusEnum> termineesStatuses);

    @Query("""
            SELECT COUNT(i) FROM Intervention i
            WHERE i.status IN :reussiesStatuses
            AND i.interventionDate >= :since
            AND (:agentId = -1 OR i.agent.id = :agentId)
            AND (:criticalityId = -1 OR i.problem.criticality.id = :criticalityId)
            """)
    Long countReussies(@Param("since") LocalDateTime since,
                       @Param("agentId") long agentId,
                       @Param("criticalityId") long criticalityId,
                       @Param("reussiesStatuses") List<InterventionStatusEnum> reussiesStatuses);

    @Query("""
            SELECT COUNT(i) FROM Intervention i
            WHERE i.status = :echecStatus
            AND i.interventionDate >= :since
            AND (:agentId = -1 OR i.agent.id = :agentId)
            AND (:criticalityId = -1 OR i.problem.criticality.id = :criticalityId)
            """)
    Long countEchecs(@Param("since") LocalDateTime since,
                     @Param("agentId") long agentId,
                     @Param("criticalityId") long criticalityId,
                     @Param("echecStatus") InterventionStatusEnum echecStatus);

    @Query("""
            SELECT AVG(i.duration) FROM Intervention i
            WHERE i.duration IS NOT NULL
            AND i.interventionDate >= :since
            AND (:agentId = -1 OR i.agent.id = :agentId)
            AND (:criticalityId = -1 OR i.problem.criticality.id = :criticalityId)
            """)
    Double avgDureeMinutes(@Param("since") LocalDateTime since,
                           @Param("agentId") long agentId,
                           @Param("criticalityId") long criticalityId);

    @Query(value = """
            SELECT COUNT(i.id)
            FROM interventions i
            JOIN problems p ON i.problem_id = p.id
            JOIN criticality c ON p.criticality_id = c.id
            WHERE i.status NOT IN ('RESOLUE', 'CLOTUREE', 'ECHEC_INTERVENTION')
            AND NOW() > i.intervention_date + (c.delay_hours || ' hours')::INTERVAL
            AND i.intervention_date >= :since
            AND (:agentId = -1 OR i.agent_id = :agentId)
            AND (:criticalityId = -1 OR p.criticality_id = :criticalityId)
            """, nativeQuery = true)
    Long countEnRetard(@Param("since") LocalDateTime since,
                       @Param("agentId") long agentId,
                       @Param("criticalityId") long criticalityId);

    @Query(value = """
            SELECT
                u.id                                                                 AS agent_id,
                u.nom                                                                AS nom,
                u.prenom                                                             AS prenom,
                COUNT(i.id)                                                          AS total,
                SUM(CASE WHEN i.status IN ('RESOLUE','CLOTUREE') THEN 1 ELSE 0 END) AS reussies,
                SUM(CASE WHEN i.status IN ('AFFECTEE','EN_COURS') THEN 1 ELSE 0 END) AS en_cours
            FROM interventions i
            JOIN users u    ON i.agent_id   = u.id
            JOIN problems p ON i.problem_id = p.id
            WHERE i.intervention_date >= :since
            AND (:criticalityId = -1 OR p.criticality_id = :criticalityId)
            GROUP BY u.id, u.nom, u.prenom
            ORDER BY total DESC
            """, nativeQuery = true)
    List<Object[]> performanceParAgent(@Param("since") LocalDateTime since,
                                       @Param("criticalityId") long criticalityId);
}