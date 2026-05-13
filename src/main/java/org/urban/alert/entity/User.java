package org.urban.alert.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;
import lombok.RequiredArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NonNull
    @Column(name = "email", length = 100)
    private String email;

    @NonNull
    @Pattern(
            regexp = "^(?:\\+212|0)[67]\\d{8}$",
            message = "Phone must be a valid Moroccan number (e.g. 0612345678 or +212612345678)"
            )
    @Column(name = "phone", length = 15, nullable = false, unique = true)
    private String phone;

    @NonNull
    @Column(name = "nom", length = 50, nullable = false)
    private String nom;

    @NonNull
    @Column(name = "prenom", length = 50, nullable = false)
    private String prenom;

    @NonNull
    @Column(name = "date_naissance", nullable = false)
    private LocalDate dateNaissance;

    @NonNull
    @Column(name = "ville", length = 100, nullable = false)
    private String ville;

    @NonNull
    @Column(name = "password", length = 72, nullable = false)
    private String password;

    @NonNull
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private UserStatus status = UserStatus.PENDING_PHONE_VERIFICATION;

    @Column(name = "phone_verified", nullable = false)
    private boolean phoneVerified = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supervisor_id")
    private User supervisor;

    @Column(name = "created_at", updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;
}
