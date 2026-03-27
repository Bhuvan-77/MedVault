package com.medvault.medvault.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.medvault.medvault.entity.MedicalRecord;
import com.medvault.medvault.entity.PatientProfile;

public interface MedicalRecordRepository
        extends JpaRepository<MedicalRecord, Long> {

    List<MedicalRecord> findByPatient(PatientProfile patient);

    java.util.Optional<MedicalRecord> findByIdAndPatient(Long id, PatientProfile patient);

    List<MedicalRecord> findBySharedWithDoctorsTrue();

    List<MedicalRecord> findByPatientAndSharedWithDoctorsTrue(PatientProfile patient);

    List<MedicalRecord> findByPatientAndIdIn(PatientProfile patient, List<Long> ids);

}