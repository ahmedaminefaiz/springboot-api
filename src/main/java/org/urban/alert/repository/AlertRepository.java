package org.urban.alert.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.urban.alert.entity.Alert;
import org.urban.alert.entity.enums.AlertStatusEnum;

import java.util.List;
import java.util.Optional;

@Repository
public interface AlertRepository extends JpaRepository<Alert, Long> {

    Page<Alert> findByUserId(Long userId, Pageable pageable);

    Page<Alert> findByStatus(AlertStatusEnum status, Pageable pageable);

    Page<Alert> findByCategoryId(Long categoryId, Pageable pageable);

    List<Alert> findByUserIdAndStatus(Long userId, AlertStatusEnum status);

    @Query("SELECT a FROM Alert a WHERE a.user.id = :userId AND a.status = :status")
    List<Alert> findUserAlertsByStatus(@Param("userId") Long userId, @Param("status") AlertStatusEnum status);

    @Query("SELECT a FROM Alert a WHERE a.category.id = :categoryId ORDER BY a.createdAt DESC")
    List<Alert> findByCategoryIdOrderByCreatedAtDesc(@Param("categoryId") Long categoryId);

    @Query("SELECT a FROM Alert a WHERE LOWER(a.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(a.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Alert> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    Optional<Alert> findByIdAndStatusNot(Long id, AlertStatusEnum status);

    List<Alert> findByProblemId(Long problemId);

    Optional<Alert> findByIdAndUserId(Long id, Long userId);

    Long countByStatus(AlertStatusEnum status);

    Long countByUserId(Long userId);
}
