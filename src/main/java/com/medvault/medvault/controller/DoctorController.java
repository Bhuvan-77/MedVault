package com.medvault.medvault.controller;

import java.time.LocalDate;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.medvault.medvault.dto.CompleteAppointmentPrescriptionRequest;
import com.medvault.medvault.dto.DoctorProfileRequest;
import com.medvault.medvault.dto.DoctorRecordRequestCreateRequest;
import com.medvault.medvault.dto.DoctorVerificationRequest;
import com.medvault.medvault.repository.DoctorProfileRepository;
import com.medvault.medvault.service.AuthService;

@CrossOrigin(origins = "http://localhost:3001")
@RestController
@RequestMapping("/api/doctor")
public class DoctorController {

    private final AuthService authService;
    private final DoctorProfileRepository doctorProfileRepository;

    public DoctorController(AuthService authService, DoctorProfileRepository doctorProfileRepository) {
        this.authService = authService;
        this.doctorProfileRepository = doctorProfileRepository;
    }

    @PostMapping("/profile")
    public String createProfile(
            @RequestParam String email,
            @RequestBody DoctorProfileRequest request) {

        return authService.createDoctorProfile(email, request);
    }
    @GetMapping("/profile/{email}")
    public ResponseEntity<?> getDoctorProfile(@PathVariable String email) {
        return ResponseEntity.ok(authService.getDoctorProfile(email));
    }

    @PutMapping("/profile/{email}")
    public ResponseEntity<?> updateDoctorProfile(
            @PathVariable String email,
            @RequestBody DoctorProfileRequest request) {

        return ResponseEntity.ok(authService.updateDoctorProfile(email, request));
    }
    @GetMapping("/appointments/{email}")
    public ResponseEntity<?> getDoctorAppointments(@PathVariable String email) {
        return ResponseEntity.ok(authService.getDoctorAppointments(email));
    }

    @PostMapping("/appointments/{appointmentId}/record-requests")
    public ResponseEntity<?> createRecordAccessRequest(
            @PathVariable Long appointmentId,
            @RequestParam String doctorEmail,
            @RequestBody DoctorRecordRequestCreateRequest request) {

        return ResponseEntity.ok(authService.createRecordAccessRequest(doctorEmail, appointmentId, request));
    }

    @GetMapping("/record-requests/{email}")
    public ResponseEntity<?> getDoctorRecordAccessRequests(@PathVariable String email) {
        return ResponseEntity.ok(authService.getDoctorRecordAccessRequests(email));
    }

    @PutMapping("/appointment/{id}")
    public ResponseEntity<?> updateAppointmentStatus(
            @PathVariable Long id,
            @RequestParam String status) {

        return ResponseEntity.ok(authService.updateAppointmentStatus(id, status));
    }

    @PostMapping("/appointment/{id}/complete")
    public ResponseEntity<?> completeAppointmentWithPrescription(
            @PathVariable Long id,
            @RequestBody CompleteAppointmentPrescriptionRequest request) {

        return ResponseEntity.ok(authService.completeAppointmentWithPrescription(id, request));
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllDoctors() {
        return ResponseEntity.ok(doctorProfileRepository.findAll());
    }

    @GetMapping("/recommend")
    public ResponseEntity<?> recommendDoctors(
            @RequestParam String description){

        return ResponseEntity.ok(
                authService.recommendDoctors(description)
        );
    }

    @GetMapping("/slots/{doctorId}")
    public ResponseEntity<?> getAvailableSlots(
            @PathVariable Long doctorId,
            @RequestParam String date) {

        LocalDate selectedDate = LocalDate.parse(date);

        return ResponseEntity.ok(
                authService.getAvailableSlots(doctorId, selectedDate)
        );
    }

    @GetMapping("/shared-records/{email}")
    public ResponseEntity<?> getSharedRecords(@PathVariable String email) {
        return ResponseEntity.ok(authService.getSharedRecordsForDoctor(email));
    }

    @PostMapping("/prescriptions/upload")
    public ResponseEntity<?> uploadPrescription(
            @RequestParam String doctorEmail,
            @RequestParam String patientEmail,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) MultipartFile file) {

        return ResponseEntity.ok(
                authService.uploadPrescription(doctorEmail, patientEmail, title, description, file)
        );
    }

    @GetMapping("/prescriptions/{email}")
    public ResponseEntity<?> getDoctorPrescriptions(@PathVariable String email) {
        return ResponseEntity.ok(authService.getDoctorPrescriptions(email));
    }

    @GetMapping("/feedbacks/{email}")
    public ResponseEntity<?> getDoctorFeedbacks(@PathVariable String email) {
        return ResponseEntity.ok(authService.getDoctorFeedbacks(email));
    }

    @PostMapping(value = "/verification", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> submitVerification(
            @RequestParam String email,
            @RequestParam String qualificationDetails,
            @RequestParam String practiceDetails,
            @RequestParam(required = false) String certificateInfo,
            @RequestParam String registrationNumber,
            @RequestParam(required = false) String additionalInfo,
            @RequestParam(required = false) MultipartFile certificateFile) {

        DoctorVerificationRequest request = new DoctorVerificationRequest();
        request.setQualificationDetails(qualificationDetails);
        request.setPracticeDetails(practiceDetails);
        request.setCertificateInfo(certificateInfo);
        request.setRegistrationNumber(registrationNumber);
        request.setAdditionalInfo(additionalInfo);

        return ResponseEntity.ok(authService.submitVerification(email, request, certificateFile));
    }

    @GetMapping("/verification/{email}")
    public ResponseEntity<?> getVerificationStatus(@PathVariable String email) {
        Object result = authService.getVerificationStatus(email);
        if (result == null) return ResponseEntity.ok(null);
        return ResponseEntity.ok(result);
    }
}
