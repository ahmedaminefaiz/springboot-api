package org.urban.alert.entity;

import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.urban.alert.entity.enums.InterventionStatusEnum;
import org.urban.alert.entity.enums.InterventionActionTypeEnum;

@Entity
@Table(name = "interventions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Intervention {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id", nullable = false)
    private Problem problem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agent_id", nullable = false)
    private User agent;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "action_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private InterventionActionTypeEnum actionType;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private InterventionStatusEnum status;

    @Column(name = "intervention_date", nullable = false, updatable = false)
    private LocalDateTime interventionDate;

    @Column
    private Integer duration;

    @Builder.Default
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "intervention_photos", joinColumns = @JoinColumn(name = "intervention_id"))
    @Column(name = "photo_url", length = 2048)
    private List<String> photos = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.interventionDate = LocalDateTime.now();
        this.status = InterventionStatusEnum.AFFECTEE;
    }

    public boolean isCloturee() {
        return InterventionStatusEnum.CLOTUREE.equals(this.status);
    }
}
