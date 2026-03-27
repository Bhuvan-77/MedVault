package com.medvault.medvault.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "access_permissions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccessPermission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Patient who gives access
    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private User patient;

    // Doctor who receives access
    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    private User doctor;

    @Column(nullable = false)
    private boolean granted = true;

    private LocalDateTime grantedAt = LocalDateTime.now();
}
