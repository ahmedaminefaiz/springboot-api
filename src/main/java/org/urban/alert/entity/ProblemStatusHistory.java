package org.urban.alert.entity;

import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import org.urban.alert.entity.enums.ProblemStatusEnum;

@Entity
@Table(name = "problem_status_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProblemStatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ProblemStatusEnum previousStatus;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ProblemStatusEnum newStatus;

    @Column(name = "changed_by", nullable = false)
    private String changedBy; // Numéro de téléphone ou ID de l'utilisateur qui a changé le statut

    @Column(columnDefinition = "TEXT")
    private String comment; // Raison du changement (optionnel)

    @Column(name = "changed_at", nullable = false, updatable = false)
    private LocalDateTime changedAt;

    // ========== Relations ==========

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id", nullable = false)
    private Problem problem;

    // ========== Lifecycle Callbacks ==========

    @PrePersist
    protected void onCreate() {
        this.changedAt = LocalDateTime.now();
    }
}