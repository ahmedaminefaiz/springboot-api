package org.urban.alert.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "criticality")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Criticality {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(name = "delay_hours", nullable = false)
    private Integer delayHours;
}