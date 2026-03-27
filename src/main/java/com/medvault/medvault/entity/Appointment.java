package com.medvault.medvault.entity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;

@Getter

@Entity
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "doctor_id")
    private DoctorProfile doctor;

    @ManyToOne
    @JoinColumn(name = "patient_id")
    private PatientProfile patient;

    private LocalDate date;
    private LocalTime time;

    private String status; // PENDING, APPROVED, COMPLETED, REJECTED
    private String description;

    @ElementCollection
    @CollectionTable(name = "appointment_shared_record_ids", joinColumns = @JoinColumn(name = "appointment_id"))
    @Column(name = "record_id")
    private List<Long> sharedRecordIds = new ArrayList<>();
    // ===== Getters and Setters =====

    public void setDoctor(DoctorProfile doctor) {
        this.doctor = doctor;
    }

    public void setPatient(PatientProfile patient) {
        this.patient = patient;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public void setSharedRecordIds(List<Long> sharedRecordIds) {
        this.sharedRecordIds = sharedRecordIds;
    }
}