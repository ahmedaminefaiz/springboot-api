package org.urban.alert.entity;

import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.urban.alert.entity.enums.InterventionStatusEnum;

@Entity
@Table(name = "intervention_updates")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterventionUpdate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "intervention_id", nullable = false)
    private Intervention intervention;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String rapport;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private InterventionStatusEnum status;

    @Builder.Default
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "intervention_update_photos", joinColumns = @JoinColumn(name = "intervention_update_id"))
    @Column(name = "photo_url", length = 2048)
    private List<String> photos = new ArrayList<>();

    @Column(name = "status_date", nullable = false)
    private LocalDateTime statusDate;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
