package com.medvault.medvault.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "doctor_verifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DoctorVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "doctor_profile_id", nullable = false, unique = true)
    private DoctorProfile doctorProfile;

    @Column(length = 2000)
    private String qualificationDetails;

    @Column(length = 2000)
    private String practiceDetails;

    @Column(length = 2000)
    private String certificateInfo;

    @Column(length = 1000)
    private String certificateFilePath;

    private String registrationNumber;

    @Column(length = 2000)
    private String additionalInfo;

    // PENDING, VERIFIED, REJECTED
    @Column(nullable = false)
    private String status = "PENDING";

    @Column(length = 1000)
    private String rejectionReason;

    private LocalDateTime submittedAt = LocalDateTime.now();

    private LocalDateTime reviewedAt;
}
