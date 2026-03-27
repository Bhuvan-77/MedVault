package com.medvault.medvault.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "doctor_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DoctorProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String gender;
    private String phoneNumber;

    private String specialization;
    private String qualification;
    private Integer experience;
    private Double consultationFee;

    private String hospitalName;
    private String state;
    private String city;

    private String availableDays;
    private String availableTime;

    private String registrationNumber;

    @Column(length = 1000)
    private String bio;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;
}