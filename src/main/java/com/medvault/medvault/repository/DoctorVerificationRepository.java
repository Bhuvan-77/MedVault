package com.medvault.medvault.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.medvault.medvault.entity.DoctorProfile;
import com.medvault.medvault.entity.DoctorVerification;

public interface DoctorVerificationRepository extends JpaRepository<DoctorVerification, Long> {

    Optional<DoctorVerification> findByDoctorProfile(DoctorProfile doctorProfile);
}
