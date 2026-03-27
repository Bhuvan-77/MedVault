package com.medvault.medvault.controller;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.medvault.medvault.dto.AppointmentRequest;
import com.medvault.medvault.dto.HealthRecordRequest;
import com.medvault.medvault.dto.MedicalRecordRequest;
import com.medvault.medvault.dto.PatientProfileRequest;
import com.medvault.medvault.dto.PatientRecordRequestResponseRequest;
import com.medvault.medvault.dto.PrescriptionFeedbackRequest;
import com.medvault.medvault.service.AuthService;
@CrossOrigin(origins = "http://localhost:3001")
@RestController
@RequestMapping("/api/patient")
public class PatientController {

    private final AuthService authService;

    public PatientController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/record")
    public String addMedicalRecord(
            @RequestParam String email,
            @RequestBody MedicalRecordRequest request) {

        return authService.addMedicalRecord(email, request);
    }

    @GetMapping("/records")
    public ResponseEntity<?> getMedicalRecords(@RequestParam String email) {
        return ResponseEntity.ok(authService.getPatientRecords(email));
    }

    @GetMapping("/records/{email}")
    public ResponseEntity<?> getPatientRecords(@PathVariable String email) {
        return ResponseEntity.ok(authService.getPatientRecords(email));
    }

    @PostMapping("/records/upload")
    public ResponseEntity<?> uploadRecord(
            @RequestParam String email,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String category,
            @RequestParam MultipartFile file) {

        return ResponseEntity.ok(authService.uploadPatientRecord(email, title, description, category, file));
    }

    @DeleteMapping("/records/{recordId}")
    public ResponseEntity<?> deleteRecord(
            @PathVariable Long recordId,
            @RequestParam String email) {

        authService.deletePatientRecord(email, recordId);
        return ResponseEntity.ok("Record deleted successfully");
    }

    @PutMapping("/records/{recordId}/share")
    public ResponseEntity<?> updateRecordSharing(
            @PathVariable Long recordId,
            @RequestParam String email,
            @RequestParam boolean shared) {

        return ResponseEntity.ok(authService.updateRecordSharing(email, recordId, shared));
    }

    @GetMapping("/prescriptions")
    public ResponseEntity<?> getPatientPrescriptions(@RequestParam String email) {
        return ResponseEntity.ok(authService.getPatientPrescriptions(email));
    }

    @PostMapping("/health")
    public ResponseEntity<?> addHealthRecord(
            @RequestParam String email,
            @RequestBody HealthRecordRequest request) {
        return ResponseEntity.ok(authService.addHealthRecord(email, request));
    }

    @GetMapping("/health")
    public ResponseEntity<?> getHealthRecords(@RequestParam String email) {
        return ResponseEntity.ok(authService.getPatientHealthRecords(email));
    }

    @PutMapping("/prescriptions/{prescriptionId}/feedback")
    public ResponseEntity<?> submitPrescriptionFeedback(
            @PathVariable Long prescriptionId,
            @RequestParam String email,
            @RequestBody PrescriptionFeedbackRequest request) {

        return ResponseEntity.ok(
                authService.submitPrescriptionFeedback(email, prescriptionId, request.getRating(), request.getFeedback())
        );
    }

    @GetMapping("/files/{category}/{fileName:.+}")
    public ResponseEntity<Resource> getUploadedFile(
            @PathVariable String category,
            @PathVariable String fileName) {
        try {
            Path path = Paths.get("uploads", category, fileName);
            Resource resource = new UrlResource(path.toUri());
            if (!resource.exists()) {
                return ResponseEntity.notFound().build();
            }

            MediaType mediaType = MediaTypeFactory.getMediaType(fileName)
                    .orElseGet(() -> {
                        try {
                            String detectedType = java.nio.file.Files.probeContentType(path);
                            return detectedType != null
                                    ? MediaType.parseMediaType(detectedType)
                                    : MediaType.APPLICATION_OCTET_STREAM;
                        } catch (IOException ignored) {
                            return MediaType.APPLICATION_OCTET_STREAM;
                        }
                    });

            return ResponseEntity.ok()
                    .contentType(mediaType)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"")
                    .body(resource);
        } catch (MalformedURLException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/profile")
    public String createProfile(
            @RequestParam String email,
            @RequestBody PatientProfileRequest request) {

        return authService.createPatientProfile(email, request);
    }

    @GetMapping("/profile/{email}")
    public ResponseEntity<?> getPatientProfile(@PathVariable String email) {

        return ResponseEntity.ok(authService.getPatientProfile(email));
    }
    @PostMapping("/appointment/{email}")
    public ResponseEntity<?> bookAppointment(
            @PathVariable String email,
            @RequestBody AppointmentRequest request) {

        return ResponseEntity.ok(authService.bookAppointment(email, request));
    }
    @GetMapping("/appointments/{email}")
    public ResponseEntity<?> getPatientAppointments(@PathVariable String email) {
        return ResponseEntity.ok(authService.getPatientAppointments(email));
    }

    @GetMapping("/record-requests")
    public ResponseEntity<?> getPatientRecordAccessRequests(@RequestParam String email) {
        return ResponseEntity.ok(authService.getPatientRecordAccessRequests(email));
    }

    @PutMapping("/record-requests/{requestId}/respond")
    public ResponseEntity<?> respondToRecordAccessRequest(
            @PathVariable Long requestId,
            @RequestParam String email,
            @RequestBody PatientRecordRequestResponseRequest request) {

        return ResponseEntity.ok(authService.respondToRecordAccessRequest(email, requestId, request));
    }
}