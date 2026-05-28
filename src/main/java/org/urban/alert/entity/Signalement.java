package org.urban.alert.entity;

import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "signalements")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Signalement {

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
    private SignalementStatus status;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SignalementPriority priority;

    @Column(name = "is_anonymous", nullable = false)
    private Boolean isAnonymous;

    @Column(columnDefinition = "LONGTEXT")
    private String images; // JSON array of image URLs

    @Column(columnDefinition = "LONGTEXT")
    private String videos; // JSON array of video URLs

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ========== Relations ==========

    // ManyToOne: Plusieurs signalements pour un utilisateur
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // ManyToOne: Plusieurs signalements pour une catégorie
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private ProblemType category;

    // OneToOne: Un signalement pour un ticket (nullable)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", nullable = true)
    private Ticket ticket;

    // ManyToMany: Un signalement peut avoir plusieurs commentaires, un commentaire peut être lié à plusieurs signalements
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(
        name = "signalement_commentaire",
        joinColumns = @JoinColumn(name = "signalement_id"),
        inverseJoinColumns = @JoinColumn(name = "commentaire_id")
    )
    private List<Commentaire> commentaires = new ArrayList<>();

    // ========== Lifecycle Callbacks ==========

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = SignalementStatus.NEW;
        }
        if (this.isAnonymous == null) {
            this.isAnonymous = false;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // ========== Helper Methods ==========

    public boolean isNew() {
        return SignalementStatus.NEW.equals(this.status);
    }

    public boolean canBeModified() {
        return SignalementStatus.NEW.equals(this.status);
    }

    public boolean canBeDeleted() {
        return SignalementStatus.NEW.equals(this.status);
    }
}
