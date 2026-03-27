package com.medvault.medvault.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.medvault.medvault.entity.Appointment;
import com.medvault.medvault.entity.DoctorProfile;
import com.medvault.medvault.entity.PatientProfile;
import com.medvault.medvault.entity.RecordAccessRequest;

public interface RecordAccessRequestRepository extends JpaRepository<RecordAccessRequest, Long> {

    List<RecordAccessRequest> findByDoctorOrderByRequestedAtDesc(DoctorProfile doctor);

    List<RecordAccessRequest> findByPatientOrderByRequestedAtDesc(PatientProfile patient);

    List<RecordAccessRequest> findByAppointmentAndStatus(Appointment appointment, String status);

    List<RecordAccessRequest> findByAppointmentAndDoctorAndStatus(Appointment appointment, DoctorProfile doctor, String status);
}
