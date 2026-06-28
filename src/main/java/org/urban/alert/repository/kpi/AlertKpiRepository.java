package org.urban.alert.repository.kpi;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.urban.alert.entity.Alert;

import java.time.LocalDateTime;

@Repository
public interface AlertKpiRepository extends JpaRepository<Alert, Long> {

    @Query("""
            SELECT COUNT(a) FROM Alert a
            WHERE a.createdAt >= :since
            AND (:problemTypeId = -1 OR a.category.id = :problemTypeId)
            """)
    Long countTotal(@Param("since") LocalDateTime since,
                    @Param("problemTypeId") long problemTypeId);

    @Query("""
            SELECT COUNT(a) FROM Alert a
            WHERE a.problem IS NULL
            AND a.createdAt >= :since
            AND (:problemTypeId = -1 OR a.category.id = :problemTypeId)
            """)
    Long countEnAttente(@Param("since") LocalDateTime since,
                        @Param("problemTypeId") long problemTypeId);

    @Query("""
            SELECT COUNT(a) FROM Alert a
            WHERE a.problem IS NOT NULL
            AND a.createdAt >= :since
            AND (:problemTypeId = -1 OR a.category.id = :problemTypeId)
            """)
    Long countQualifiees(@Param("since") LocalDateTime since,
                         @Param("problemTypeId") long problemTypeId);

    @Query(value = """
            SELECT AVG(EXTRACT(EPOCH FROM (a.qualified_at - a.created_at)) / 3600.0)
            FROM alerts a
            WHERE a.qualified_at IS NOT NULL
            AND a.created_at >= :since
            AND (:problemTypeId = -1 OR a.category_id = :problemTypeId)
            """, nativeQuery = true)
    Double avgDelaiQualificationHeures(@Param("since") LocalDateTime since,
                                       @Param("problemTypeId") long problemTypeId);
}