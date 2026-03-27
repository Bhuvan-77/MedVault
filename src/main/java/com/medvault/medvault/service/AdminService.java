package com.medvault.medvault.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.medvault.medvault.entity.Appointment;
import com.medvault.medvault.entity.DoctorProfile;
import com.medvault.medvault.entity.DoctorVerification;
import com.medvault.medvault.entity.MedicalRecord;
import com.medvault.medvault.entity.PatientProfile;
import com.medvault.medvault.enums.Role;
import com.medvault.medvault.repository.AppointmentRepository;
import com.medvault.medvault.repository.DoctorProfileRepository;
import com.medvault.medvault.repository.DoctorVerificationRepository;
import com.medvault.medvault.repository.HealthRecordRepository;
import com.medvault.medvault.repository.MedicalRecordRepository;
import com.medvault.medvault.repository.PatientProfileRepository;
import com.medvault.medvault.repository.UserRepository;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final DoctorProfileRepository doctorProfileRepository;
    private final PatientProfileRepository patientProfileRepository;
    private final AppointmentRepository appointmentRepository;
    private final MedicalRecordRepository medicalRecordRepository;
    private final HealthRecordRepository healthRecordRepository;
    private final DoctorVerificationRepository doctorVerificationRepository;

    public AdminService(
            UserRepository userRepository,
            DoctorProfileRepository doctorProfileRepository,
            PatientProfileRepository patientProfileRepository,
            AppointmentRepository appointmentRepository,
            MedicalRecordRepository medicalRecordRepository,
            HealthRecordRepository healthRecordRepository,
            DoctorVerificationRepository doctorVerificationRepository) {
        this.userRepository = userRepository;
        this.doctorProfileRepository = doctorProfileRepository;
        this.patientProfileRepository = patientProfileRepository;
        this.appointmentRepository = appointmentRepository;
        this.medicalRecordRepository = medicalRecordRepository;
        this.healthRecordRepository = healthRecordRepository;
        this.doctorVerificationRepository = doctorVerificationRepository;
    }

    // ===== Dashboard Overview =====

    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalPatients", countUsersByRole(Role.PATIENT));
        stats.put("totalDoctors", countUsersByRole(Role.DOCTOR));
        stats.put("totalAppointments", appointmentRepository.count());
        stats.put("pendingAppointments", countAppointmentsByStatus("PENDING"));
        long verifiedDoctors = doctorVerificationRepository.findAll().stream()
                .filter(v -> "VERIFIED".equals(v.getStatus())).count();
        stats.put("verifiedDoctors", verifiedDoctors);
        stats.put("pendingVerifications", countVerificationsByStatus("PENDING"));
        return stats;
    }

    public List<Map<String, Object>> getRecentActivity(int limit) {
        List<Map<String, Object>> activity = new ArrayList<>();

        // Recent appointments
        List<Appointment> recentAppointments = appointmentRepository.findAll()
                .stream()
                .sorted(Comparator.comparing((Appointment a) -> a.getDate() != null ? a.getDate() : java.time.LocalDate.MIN)
                        .reversed())
                .limit(limit)
                .collect(Collectors.toList());

        for (Appointment apt : recentAppointments) {
            Map<String, Object> item = new HashMap<>();
            item.put("type", "appointment");
            item.put("patientName", apt.getPatient() != null ? apt.getPatient().getUser().getName() : "Unknown");
            item.put("doctorName", apt.getDoctor() != null ? apt.getDoctor().getUser().getName() : "Unknown");
            item.put("date", apt.getDate());
            item.put("status", apt.getStatus());
            activity.add(item);
        }

        return activity.stream().limit(limit).collect(Collectors.toList());
    }

    // ===== Doctor Management =====

    public List<Map<String, Object>> getAllDoctors() {
        List<DoctorProfile> doctors = doctorProfileRepository.findAll();
        return doctors.stream().map(this::doctorToMap).collect(Collectors.toList());
    }

    private Map<String, Object> doctorToMap(DoctorProfile doctor) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", doctor.getId());
        map.put("name", doctor.getUser() != null ? doctor.getUser().getName() : "");
        map.put("email", doctor.getUser() != null ? doctor.getUser().getEmail() : "");
        map.put("specialization", doctor.getSpecialization());
        map.put("experience", doctor.getExperience());
        map.put("createdAt", doctor.getUser() != null ? doctor.getUser().getCreatedAt() : null);

        // Attach verification status
        doctorVerificationRepository.findByDoctorProfile(doctor).ifPresentOrElse(
                v -> {
                    map.put("verificationStatus", v.getStatus());
                    map.put("verificationId", v.getId());
                },
                () -> {
                    map.put("verificationStatus", "NOT_SUBMITTED");
                    map.put("verificationId", null);
                }
        );
        return map;
    }

    public void deleteDoctor(Long doctorId) {
        DoctorProfile doctor = doctorProfileRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));
        Long userId = doctor.getUser().getId();
        doctorProfileRepository.deleteById(doctorId);
        userRepository.deleteById(userId);
    }

    // ===== Patient Management =====

    public List<Map<String, Object>> getAllPatients() {
        List<PatientProfile> patients = patientProfileRepository.findAll();
        return patients.stream().map(this::patientToMap).collect(Collectors.toList());
    }

    private Map<String, Object> patientToMap(PatientProfile patient) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", patient.getId());
        map.put("name", patient.getUser() != null ? patient.getUser().getName() : "");
        map.put("email", patient.getUser() != null ? patient.getUser().getEmail() : "");
        map.put("age", patient.getAge());
        map.put("phone", patient.getPhoneNumber());
        map.put("createdAt", patient.getUser() != null ? patient.getUser().getCreatedAt() : null);
        return map;
    }

    public void deletePatient(Long patientId) {
        PatientProfile patient = patientProfileRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));
        Long userId = patient.getUser().getId();
        patientProfileRepository.deleteById(patientId);
        userRepository.deleteById(userId);
    }

    // ===== Appointment Management =====

    public List<Map<String, Object>> getAllAppointments() {
        List<Appointment> appointments = appointmentRepository.findAll();
        return appointments.stream().map(this::appointmentToMap).collect(Collectors.toList());
    }

    private Map<String, Object> appointmentToMap(Appointment apt) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", apt.getId());
        map.put("patientName", apt.getPatient() != null ? apt.getPatient().getUser().getName() : "Unknown");
        map.put("patientEmail", apt.getPatient() != null ? apt.getPatient().getUser().getEmail() : "");
        map.put("doctorName", apt.getDoctor() != null ? apt.getDoctor().getUser().getName() : "Unknown");
        map.put("doctorEmail", apt.getDoctor() != null ? apt.getDoctor().getUser().getEmail() : "");
        map.put("date", apt.getDate());
        map.put("time", apt.getTime());
        map.put("status", apt.getStatus());
        map.put("description", apt.getDescription());
        return map;
    }

    public void updateAppointmentStatus(Long appointmentId, String status) {
        Appointment apt = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        apt.setStatus(status);
        appointmentRepository.save(apt);
    }

    public void deleteAppointment(Long appointmentId) {
        appointmentRepository.deleteById(appointmentId);
    }

    // ===== Medical Records Management =====

    public List<Map<String, Object>> getAllMedicalRecords() {
        List<MedicalRecord> records = medicalRecordRepository.findAll();
        return records.stream().map(this::recordToMap).collect(Collectors.toList());
    }

    private Map<String, Object> recordToMap(MedicalRecord record) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", record.getId());
        map.put("patientName", record.getPatient() != null ? record.getPatient().getUser().getName() : "Unknown");
        map.put("patientEmail", record.getPatient() != null ? record.getPatient().getUser().getEmail() : "");
        map.put("title", record.getTitle());
        map.put("filePath", record.getFilePath());
        map.put("uploadedDate", record.getUploadedDate());
        return map;
    }

    public void deleteMedicalRecord(Long recordId) {
        medicalRecordRepository.deleteById(recordId);
    }

    // ===== Helper Methods =====

    private long countUsersByRole(Role role) {
        return userRepository.findAll()
                .stream()
                .filter(u -> u.getRole() == role)
                .count();
    }

    private long countAppointmentsByStatus(String status) {
        return appointmentRepository.findAll()
                .stream()
                .filter(a -> status.equals(a.getStatus()))
                .count();
    }

    private long countVerificationsByStatus(String status) {
        return doctorVerificationRepository.findAll().stream()
                .filter(v -> status.equals(v.getStatus())).count();
    }

    // ===== Verification Management =====

    public List<Map<String, Object>> getAllVerificationRequests() {
        return doctorVerificationRepository.findAll().stream()
                .sorted(Comparator.comparing(DoctorVerification::getSubmittedAt,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .map(this::verificationToMap)
                .collect(Collectors.toList());
    }

    private Map<String, Object> verificationToMap(DoctorVerification v) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", v.getId());
        map.put("status", v.getStatus());
        map.put("rejectionReason", v.getRejectionReason());
        map.put("submittedAt", v.getSubmittedAt());
        map.put("reviewedAt", v.getReviewedAt());
        map.put("qualificationDetails", v.getQualificationDetails());
        map.put("practiceDetails", v.getPracticeDetails());
        map.put("certificateInfo", v.getCertificateInfo());
        map.put("certificateFilePath", v.getCertificateFilePath());
        map.put("registrationNumber", v.getRegistrationNumber());
        map.put("additionalInfo", v.getAdditionalInfo());
        DoctorProfile doc = v.getDoctorProfile();
        map.put("doctorName", doc != null && doc.getUser() != null ? doc.getUser().getName() : "");
        map.put("doctorEmail", doc != null && doc.getUser() != null ? doc.getUser().getEmail() : "");
        map.put("doctorSpecialization", doc != null ? doc.getSpecialization() : "");
        map.put("doctorProfileId", doc != null ? doc.getId() : null);
        return map;
    }

    public void approveVerification(Long verificationId) {
        DoctorVerification v = doctorVerificationRepository.findById(verificationId)
                .orElseThrow(() -> new RuntimeException("Verification request not found"));
        v.setStatus("VERIFIED");
        v.setRejectionReason(null);
        v.setReviewedAt(java.time.LocalDateTime.now());
        doctorVerificationRepository.save(v);
    }

    public void rejectVerification(Long verificationId, String reason) {
        DoctorVerification v = doctorVerificationRepository.findById(verificationId)
                .orElseThrow(() -> new RuntimeException("Verification request not found"));
        v.setStatus("REJECTED");
        v.setRejectionReason(reason);
        v.setReviewedAt(java.time.LocalDateTime.now());
        doctorVerificationRepository.save(v);
    }
}
