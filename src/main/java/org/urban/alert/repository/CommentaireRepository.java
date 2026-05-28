package org.urban.alert.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.urban.alert.entity.Commentaire;

import java.util.List;

@Repository
public interface CommentaireRepository extends JpaRepository<Commentaire, Long> {

    @Query("SELECT c FROM Commentaire c JOIN c.alerts a WHERE a.id = :alertId ORDER BY c.createdAt DESC")
    List<Commentaire> findByAlertId(@Param("alertId") Long alertId);

    Page<Commentaire> findByAuthorId(Long authorId, Pageable pageable);

    Long countByAuthorId(Long authorId);
}