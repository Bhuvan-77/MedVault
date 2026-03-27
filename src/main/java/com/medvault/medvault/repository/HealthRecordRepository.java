package com.medvault.medvault.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.medvault.medvault.entity.HealthRecord;
import com.medvault.medvault.entity.PatientProfile;

public interface HealthRecordRepository extends JpaRepository<HealthRecord, Long> {
    List<HealthRecord> findByPatientOrderByRecordedAtDesc(PatientProfile patient);
}
