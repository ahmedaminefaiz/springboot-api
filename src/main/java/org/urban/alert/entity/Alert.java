package org.urban.alert.entity;

import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.urban.alert.entity.enums.AlertStatusEnum;
import org.urban.alert.entity.enums.AlertPriorityEnum;

@Entity
@Table(name = "alerts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Alert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Float latitude;

    @Column(nullable = false)
    private Float longitude;

    @Column(length = 500)
    private String address;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AlertStatusEnum status;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AlertPriorityEnum priority;

    @Column(name = "is_anonymous", nullable = false)
    private Boolean isAnonymous;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "alert_images", joinColumns = @JoinColumn(name = "alert_id"))
    @Column(name = "image_url", length = 2048)
    private List<String> images = new ArrayList<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "alert_videos", joinColumns = @JoinColumn(name = "alert_id"))
    @Column(name = "video_url", length = 2048)
    private List<String> videos = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ========== Relations ==========

    // ManyToOne: Plusieurs alertes pour un utilisateur
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // ManyToOne: Plusieurs alertes pour une catégorie
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private ProblemType category;

    // ManyToOne: Plusieurs alertes peuvent être assignées à un problème
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id", nullable = true)
    private Problem problem;

// Garder ticket_id pour la compatibilité rétroactive (optionnel)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", nullable = true)
    private Ticket ticket;

    // ManyToMany: Une alerte peut avoir plusieurs commentaires, un commentaire peut être lié à plusieurs alertes
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(
        name = "alert_commentaire",
        joinColumns = @JoinColumn(name = "alert_id"),
        inverseJoinColumns = @JoinColumn(name = "commentaire_id")
    )
    private List<Commentaire> commentaires = new ArrayList<>();

    // ========== Lifecycle Callbacks ==========

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.status == null ) {
            this.status = AlertStatusEnum.NEW;
            
        }
        if (this.isAnonymous == null) {
            this.isAnonymous = false;
        }
        if(this.priority == null) {
            this.priority = AlertPriorityEnum.MEDIUM;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // ========== Helper Methods ==========

    public boolean isNew() {
        return AlertStatusEnum.NEW.equals(this.status);
    }

    public boolean canBeModified() {
        return AlertStatusEnum.NEW.equals(this.status);
    }

    public boolean canBeDeleted() {
        return AlertStatusEnum.NEW.equals(this.status);
    }
}
