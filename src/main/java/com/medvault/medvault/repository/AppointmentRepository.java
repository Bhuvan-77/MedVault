package com.medvault.medvault.repository;

import com.medvault.medvault.entity.Appointment;
import com.medvault.medvault.entity.DoctorProfile;
import com.medvault.medvault.entity.PatientProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByDoctor(DoctorProfile doctor);

    List<Appointment> findByPatient(PatientProfile patient);

    boolean existsByDoctorAndDateAndTime(
            DoctorProfile doctor,
            LocalDate date,
            LocalTime time
    );
}