package com.medvault.medvault.entity;

import java.time.LocalDateTime;
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

@Entity
public class RecordAccessRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "appointment_id")
    private Appointment appointment;

    @ManyToOne
    @JoinColumn(name = "doctor_id")
    private DoctorProfile doctor;

    @ManyToOne
    @JoinColumn(name = "patient_id")
    private PatientProfile patient;

    private String requestType; // CATEGORY or SPECIFIC

    private String requestedCategory;

    private String requestedReportName;

    @Column(length = 1000)
    private String note;

    private String status; // PENDING, APPROVED, REJECTED

    private LocalDateTime requestedAt;

    private LocalDateTime respondedAt;

    @ElementCollection
    @CollectionTable(name = "record_request_record_ids", joinColumns = @JoinColumn(name = "request_id"))
    @Column(name = "record_id")
    private List<Long> approvedRecordIds = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Appointment getAppointment() {
        return appointment;
    }

    public void setAppointment(Appointment appointment) {
        this.appointment = appointment;
    }

    public DoctorProfile getDoctor() {
        return doctor;
    }

    public void setDoctor(DoctorProfile doctor) {
        this.doctor = doctor;
    }

    public PatientProfile getPatient() {
        return patient;
    }

    public void setPatient(PatientProfile patient) {
        this.patient = patient;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public String getRequestedCategory() {
        return requestedCategory;
    }

    public void setRequestedCategory(String requestedCategory) {
        this.requestedCategory = requestedCategory;
    }

    public String getRequestedReportName() {
        return requestedReportName;
    }

    public void setRequestedReportName(String requestedReportName) {
        this.requestedReportName = requestedReportName;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getRequestedAt() {
        return requestedAt;
    }

    public void setRequestedAt(LocalDateTime requestedAt) {
        this.requestedAt = requestedAt;
    }

    public LocalDateTime getRespondedAt() {
        return respondedAt;
    }

    public void setRespondedAt(LocalDateTime respondedAt) {
        this.respondedAt = respondedAt;
    }

    public List<Long> getApprovedRecordIds() {
        return approvedRecordIds;
    }

    public void setApprovedRecordIds(List<Long> approvedRecordIds) {
        this.approvedRecordIds = approvedRecordIds;
    }
}
