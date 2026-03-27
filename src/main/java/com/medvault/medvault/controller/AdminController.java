package com.medvault.medvault.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.medvault.medvault.service.AdminService;

@CrossOrigin(origins = "http://localhost:3001")
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    // ===== Dashboard =====

    @GetMapping("/dashboard/stats")
    public ResponseEntity<?> getDashboardStats() {
        return ResponseEntity.ok(adminService.getDashboardStats());
    }

    @GetMapping("/dashboard/activity")
    public ResponseEntity<?> getRecentActivity(@RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(adminService.getRecentActivity(limit));
    }

    // ===== Doctors =====

    @GetMapping("/doctors")
    public ResponseEntity<?> getAllDoctors() {
        return ResponseEntity.ok(adminService.getAllDoctors());
    }

    @DeleteMapping("/doctors/{doctorId}")
    public ResponseEntity<?> deleteDoctor(@PathVariable Long doctorId) {
        adminService.deleteDoctor(doctorId);
        return ResponseEntity.ok("Doctor deleted successfully");
    }

    // ===== Patients =====

    @GetMapping("/patients")
    public ResponseEntity<?> getAllPatients() {
        return ResponseEntity.ok(adminService.getAllPatients());
    }

    @DeleteMapping("/patients/{patientId}")
    public ResponseEntity<?> deletePatient(@PathVariable Long patientId) {
        adminService.deletePatient(patientId);
        return ResponseEntity.ok("Patient deleted successfully");
    }

    // ===== Appointments =====

    @GetMapping("/appointments")
    public ResponseEntity<?> getAllAppointments() {
        return ResponseEntity.ok(adminService.getAllAppointments());
    }

    @PutMapping("/appointments/{appointmentId}/status")
    public ResponseEntity<?> updateAppointmentStatus(
            @PathVariable Long appointmentId,
            @RequestParam String status) {
        adminService.updateAppointmentStatus(appointmentId, status);
        return ResponseEntity.ok("Appointment status updated successfully");
    }

    @DeleteMapping("/appointments/{appointmentId}")
    public ResponseEntity<?> deleteAppointment(@PathVariable Long appointmentId) {
        adminService.deleteAppointment(appointmentId);
        return ResponseEntity.ok("Appointment deleted successfully");
    }

    // ===== Medical Records =====

    @GetMapping("/records")
    public ResponseEntity<?> getAllMedicalRecords() {
        return ResponseEntity.ok(adminService.getAllMedicalRecords());
    }

    @DeleteMapping("/records/{recordId}")
    public ResponseEntity<?> deleteMedicalRecord(@PathVariable Long recordId) {
        adminService.deleteMedicalRecord(recordId);
        return ResponseEntity.ok("Record deleted successfully");
    }

    // ===== Verification Management =====

    @GetMapping("/verifications")
    public ResponseEntity<?> getAllVerificationRequests() {
        return ResponseEntity.ok(adminService.getAllVerificationRequests());
    }

    @PutMapping("/verifications/{id}/approve")
    public ResponseEntity<?> approveVerification(@PathVariable Long id) {
        adminService.approveVerification(id);
        return ResponseEntity.ok("Verification approved");
    }

    @PutMapping("/verifications/{id}/reject")
    public ResponseEntity<?> rejectVerification(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        adminService.rejectVerification(id, body.get("reason"));
        return ResponseEntity.ok("Verification rejected");
    }
}
