package com.medvault.medvault.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.medvault.medvault.entity.Appointment;
import com.medvault.medvault.entity.DoctorProfile;
import com.medvault.medvault.entity.PatientProfile;
import com.medvault.medvault.entity.Prescription;

public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {

    List<Prescription> findByPatientOrderByPrescribedDateDesc(PatientProfile patient);

    List<Prescription> findByDoctorOrderByPrescribedDateDesc(DoctorProfile doctor);

    List<Prescription> findByDoctorAndFeedbackRatingIsNotNullOrderByFeedbackDateDesc(DoctorProfile doctor);

    boolean existsByAppointment(Appointment appointment);

    Optional<Prescription> findByAppointment(Appointment appointment);
}
