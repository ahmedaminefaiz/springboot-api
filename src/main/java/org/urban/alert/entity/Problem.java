package org.urban.alert.entity;

import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.urban.alert.entity.enums.ProblemStatusEnum;
import org.urban.alert.entity.Criticality;

@Entity
@Table(name = "problems")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Problem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ProblemStatusEnum status;

    @Column(length = 255)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    // ========== Relations ==========

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "criticality_id", nullable = false)
    private Criticality criticality;

    // ManyToOne: Un problème créé par un SuperAgent (utilisateur)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Doit être SUPER_AGENT

    // OneToMany: Un problème peut avoir plusieurs alertes (pas de cascade : ON DELETE SET NULL en base)
    @Builder.Default
    @OneToMany(mappedBy = "problem", fetch = FetchType.LAZY)
    private List<Alert> alerts = new ArrayList<>();

    // OneToMany: Historique des changements de statut
    @Builder.Default
    @OneToMany(mappedBy = "problem", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @OrderBy("changedAt DESC")
    private List<ProblemStatusHistory> statusHistory = new ArrayList<>();

    // ========== Lifecycle Callbacks ==========

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = ProblemStatusEnum.NEW;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // ========== Helper Methods ==========

    public boolean isNew() {
        return ProblemStatusEnum.NEW.equals(this.status);
    }

    public boolean canBeModified() {
        return !ProblemStatusEnum.RESOLVED.equals(this.status) &&
                !ProblemStatusEnum.REJECTED.equals(this.status);
    }

    public void addAlert(Alert alert) {
        this.alerts.add(alert);
        alert.setProblem(this);
    }

    public void removeAlert(Alert alert) {
        this.alerts.remove(alert);
        alert.setProblem(null);
    }

    public void addStatusHistory(ProblemStatusHistory history) {
        this.statusHistory.add(history);
        history.setProblem(this);
    }
}