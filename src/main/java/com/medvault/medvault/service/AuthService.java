package com.medvault.medvault.service;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.medvault.medvault.dto.AppointmentRequest;
import com.medvault.medvault.dto.AvailableSlotResponse;
import com.medvault.medvault.dto.CompleteAppointmentPrescriptionRequest;
import com.medvault.medvault.dto.DoctorProfileRequest;
import com.medvault.medvault.dto.DoctorRecordRequestCreateRequest;
import com.medvault.medvault.dto.DoctorVerificationRequest;
import com.medvault.medvault.dto.HealthRecordRequest;
import com.medvault.medvault.dto.LoginRequest;
import com.medvault.medvault.dto.MedicalRecordRequest;
import com.medvault.medvault.dto.PatientProfileRequest;
import com.medvault.medvault.dto.PatientRecordRequestResponseRequest;
import com.medvault.medvault.dto.RegisterRequest;
import com.medvault.medvault.entity.Appointment;
import com.medvault.medvault.entity.DoctorProfile;
import com.medvault.medvault.entity.DoctorVerification;
import com.medvault.medvault.entity.HealthRecord;
import com.medvault.medvault.entity.MedicalRecord;
import com.medvault.medvault.entity.PatientProfile;
import com.medvault.medvault.entity.Prescription;
import com.medvault.medvault.entity.RecordAccessRequest;
import com.medvault.medvault.entity.User;
import com.medvault.medvault.enums.Role;
import com.medvault.medvault.repository.AppointmentRepository;
import com.medvault.medvault.repository.DoctorProfileRepository;
import com.medvault.medvault.repository.DoctorVerificationRepository;
import com.medvault.medvault.repository.HealthRecordRepository;
import com.medvault.medvault.repository.MedicalRecordRepository;
import com.medvault.medvault.repository.PatientProfileRepository;
import com.medvault.medvault.repository.PrescriptionRepository;
import com.medvault.medvault.repository.RecordAccessRequestRepository;
import com.medvault.medvault.repository.UserRepository;

@Service
public class AuthService {

    private static final Map<String, List<String>> SPECIALIZATION_KEYWORDS = Map.ofEntries(
            Map.entry("Cardiologist", List.of("chest", "heart", "palpitation", "bp", "pressure", "cardiac", "angina")),
            Map.entry("Dermatologist", List.of("skin", "rash", "acne", "itch", "allergy", "eczema", "psoriasis", "pigmentation")),
            Map.entry("Neurologist", List.of("headache", "brain", "migraine", "seizure", "numbness", "vertigo", "dizziness", "nerve")),
            Map.entry("Gastroenterologist", List.of("stomach", "gas", "acidity", "indigestion", "ulcer", "bloating", "constipation", "diarrhea", "abdomen")),
            Map.entry("ENT", List.of("cold", "cough", "throat", "sore throat", "ear", "nose", "sinus", "tonsil", "sneezing", "runny nose", "congestion")),
            Map.entry("Orthopedic", List.of("bone", "joint", "knee", "back pain", "fracture", "sprain", "arthritis", "spine")),
            Map.entry("Pulmonologist", List.of("breath", "asthma", "lung", "wheezing", "shortness of breath", "respiratory", "bronchitis")),
            Map.entry("Ophthalmologist", List.of("eye", "vision", "blurred vision", "cataract", "glaucoma", "red eye", "watering")),
            Map.entry("Gynecologist", List.of("period", "pregnancy", "menstrual", "pcos", "uterus", "ovary", "vaginal", "gyne")),
            Map.entry("Urologist", List.of("urine", "kidney stone", "bladder", "prostate", "urinary", "burning urination")),
            Map.entry("Nephrologist", List.of("kidney", "creatinine", "dialysis", "renal", "proteinuria", "swelling")),
            Map.entry("Endocrinologist", List.of("thyroid", "diabetes", "hormone", "sugar", "insulin", "metabolism")),
            Map.entry("Psychiatrist", List.of("anxiety", "depression", "panic", "sleep", "stress", "mood", "mental")),
            Map.entry("Pediatrician", List.of("child", "baby", "infant", "newborn", "pediatric", "vaccination")),
            Map.entry("Dentist", List.of("tooth", "teeth", "gum", "dental", "cavity", "jaw", "oral"))
    );

    private static final Map<String, List<String>> SPECIALIZATION_ALIASES = Map.ofEntries(
            Map.entry("Cardiologist", List.of("cardiologist", "cardio", "heart specialist")),
            Map.entry("Dermatologist", List.of("dermatologist", "derma", "skin specialist")),
            Map.entry("Neurologist", List.of("neurologist", "neuro", "nerve specialist")),
            Map.entry("Gastroenterologist", List.of("gastroenterologist", "gastro", "gi", "stomach specialist")),
            Map.entry("ENT", List.of("ent", "otolaryng", "ear nose throat")),
            Map.entry("Orthopedic", List.of("orthopedic", "orthopaedic", "ortho", "bone specialist")),
            Map.entry("Pulmonologist", List.of("pulmonologist", "pulmonary", "chest physician", "respiratory specialist")),
            Map.entry("Ophthalmologist", List.of("ophthalmologist", "ophthal", "eye specialist")),
            Map.entry("Gynecologist", List.of("gynecologist", "gynaecologist", "obgyn", "ob gyn")),
            Map.entry("Urologist", List.of("urologist", "uro")),
            Map.entry("Nephrologist", List.of("nephrologist", "nephro", "renal specialist")),
            Map.entry("Endocrinologist", List.of("endocrinologist", "endocrine", "hormone specialist")),
            Map.entry("Psychiatrist", List.of("psychiatrist", "mental health", "psych")),
            Map.entry("Pediatrician", List.of("pediatrician", "paediatrician", "child specialist", "peds")),
            Map.entry("Dentist", List.of("dentist", "dental", "oral specialist"))
    );

    private static final Set<String> GENERIC_SYMPTOMS = Set.of(
        "fever", "weakness", "pain", "tired", "fatigue", "body", "infection", "sick", "unwell"
    );

    private static final Set<String> ALLOWED_RECORD_CATEGORIES = Set.of(
            "test_reports",
            "diagnoses",
            "prescriptions",
            "imaging",
            "other"
    );

    private final UserRepository userRepository;
    private final PatientProfileRepository patientProfileRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final MedicalRecordRepository medicalRecordRepository;
    private final HealthRecordRepository healthRecordRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final DoctorProfileRepository doctorProfileRepository;
    private final AppointmentRepository appointmentRepository;
    private final DoctorVerificationRepository doctorVerificationRepository;
    private final RecordAccessRequestRepository recordAccessRequestRepository;
    private final NotificationService notificationService;

    public AuthService(UserRepository userRepository,
                       PatientProfileRepository patientProfileRepository,
                       MedicalRecordRepository medicalRecordRepository,
                       HealthRecordRepository healthRecordRepository,
                       PrescriptionRepository prescriptionRepository,
                       DoctorProfileRepository doctorProfileRepository,
                       AppointmentRepository appointmentRepository,
                       DoctorVerificationRepository doctorVerificationRepository,
                       RecordAccessRequestRepository recordAccessRequestRepository,
                       NotificationService notificationService) {

        this.userRepository = userRepository;
        this.patientProfileRepository = patientProfileRepository;
        this.medicalRecordRepository = medicalRecordRepository;
        this.healthRecordRepository = healthRecordRepository;
        this.prescriptionRepository = prescriptionRepository;
        this.doctorProfileRepository = doctorProfileRepository;
        this.appointmentRepository = appointmentRepository;
        this.doctorVerificationRepository = doctorVerificationRepository;
        this.recordAccessRequestRepository = recordAccessRequestRepository;
        this.notificationService = notificationService;
    }

    public String register(RegisterRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return "Email already exists!";
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());

        userRepository.save(user);

        return "User Registered Successfully!";
    }



    public Map<String, Object> login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        boolean profileCompleted = false;

        if (user.getRole() == Role.PATIENT) {
            profileCompleted = patientProfileRepository.findByUser(user).isPresent();
        }

        if (user.getRole() == Role.DOCTOR) {
            profileCompleted = doctorProfileRepository.findByUser(user).isPresent();
        }

        Map<String, Object> response = new HashMap<>();
        response.put("email", user.getEmail());

        response.put("name", user.getName());
        response.put("role", user.getRole().name());
        response.put("profileCompleted", profileCompleted);

        return response;
    }

    public String createPatientProfile(String email, PatientProfileRequest request) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getRole() != Role.PATIENT) {
            throw new RuntimeException("Only patients can create profile");
        }

        if (patientProfileRepository.findByUser(user).isPresent()) {
            throw new RuntimeException("Profile already exists");
        }

        PatientProfile profile = new PatientProfile();
        profile.setName(request.getName());
        profile.setAge(request.getAge());
        profile.setGender(request.getGender());
        profile.setPhoneNumber(request.getPhoneNumber());
        profile.setUser(user);

        patientProfileRepository.save(profile);

        return "Patient Profile Created Successfully!";
    }

    public String addMedicalRecord(String email, MedicalRecordRequest request) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getRole() != Role.PATIENT) {
            throw new RuntimeException("Only patients can add records");
        }

        PatientProfile profile = (PatientProfile) patientProfileRepository
                .findByUser(user)
                .orElseThrow(() -> new RuntimeException("Patient profile not found"));

        MedicalRecord record = new MedicalRecord();
        record.setTitle(request.getDiagnosis());
        record.setDescription(request.getDoctorNotes());
        record.setFilePath(null);
        record.setCategory("diagnoses");
        record.setUploadedDate(LocalDate.parse(request.getVisitDate()));
        record.setPatient(profile);
        record.setPatientId(profile.getId());

        medicalRecordRepository.save(record);

        return "Medical Record Added Successfully!";
    }

    public List<MedicalRecord> getPatientRecords(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        PatientProfile profile = (PatientProfile) patientProfileRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Patient profile not found"));

        return medicalRecordRepository.findByPatient(profile);
    }

    public List<MedicalRecord> getMedicalRecords(String email) {
        return getPatientRecords(email);
    }

    public HealthRecord addHealthRecord(String email, HealthRecordRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        PatientProfile profile = (PatientProfile) patientProfileRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Patient profile not found"));

        double heightM = request.getHeightCm() / 100.0;
        double bmi = request.getWeightKg() / (heightM * heightM);
        bmi = Math.round(bmi * 100.0) / 100.0;

        HealthRecord record = new HealthRecord();
        record.setHeightCm(request.getHeightCm());
        record.setWeightKg(request.getWeightKg());
        record.setSystolicBp(request.getSystolicBp());
        record.setDiastolicBp(request.getDiastolicBp());
        record.setHeartRate(request.getHeartRate());
        record.setBloodSugar(request.getBloodSugar());
        record.setBmi(bmi);
        record.setPatient(profile);

        return healthRecordRepository.save(record);
    }

    public List<HealthRecord> getPatientHealthRecords(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        PatientProfile profile = (PatientProfile) patientProfileRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Patient profile not found"));
        return healthRecordRepository.findByPatientOrderByRecordedAtDesc(profile);
    }

    public MedicalRecord uploadPatientRecord(String email,
                                             String title,
                                             String description,
                                             String category,
                                             MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("Record file is required");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        PatientProfile patient = patientProfileRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Patient profile not found"));

        String normalizedCategory = normalizeRecordCategory(category);
        String storedPath = storeFile(file, normalizedCategory);

        MedicalRecord record = new MedicalRecord();
        record.setTitle((title == null || title.isBlank()) ? file.getOriginalFilename() : title);
        record.setDescription(description);
        record.setFilePath(storedPath);
        record.setCategory(normalizedCategory);
        record.setUploadedDate(LocalDate.now());
        record.setPatient(patient);
        record.setPatientId(patient.getId());
        record.setSharedWithDoctors(false);

        return medicalRecordRepository.save(record);
    }

    public void deletePatientRecord(String email, Long recordId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        PatientProfile patient = patientProfileRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Patient profile not found"));

        MedicalRecord record = medicalRecordRepository.findByIdAndPatient(recordId, patient)
                .orElseThrow(() -> new RuntimeException("Record not found"));

        if ("prescriptions".equalsIgnoreCase(record.getCategory())) {
            throw new RuntimeException("Prescription records cannot be deleted");
        }

        deleteStoredFile(record.getFilePath());
        medicalRecordRepository.delete(record);
    }

    public MedicalRecord updateRecordSharing(String email, Long recordId, boolean shared) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        PatientProfile patient = patientProfileRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Patient profile not found"));

        MedicalRecord record = medicalRecordRepository.findByIdAndPatient(recordId, patient)
                .orElseThrow(() -> new RuntimeException("Record not found"));

        record.setSharedWithDoctors(shared);
        return medicalRecordRepository.save(record);
    }

    public List<MedicalRecord> getSharedRecordsForDoctor(String email) {
        userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        return medicalRecordRepository.findBySharedWithDoctorsTrue();
    }

    public Prescription uploadPrescription(String doctorEmail,
                                           String patientEmail,
                                           String title,
                                           String description,
                                           MultipartFile file) {

        User doctorUser = userRepository.findByEmail(doctorEmail)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));
        DoctorProfile doctor = doctorProfileRepository.findByUser(doctorUser)
                .orElseThrow(() -> new RuntimeException("Doctor profile not found"));

        User patientUser = userRepository.findByEmail(patientEmail)
                .orElseThrow(() -> new RuntimeException("Patient not found"));
        PatientProfile patient = patientProfileRepository.findByUser(patientUser)
                .orElseThrow(() -> new RuntimeException("Patient profile not found"));

        String storedPath = null;
        if (file != null && !file.isEmpty()) {
            storedPath = storeFile(file, "prescriptions");
        }

        Prescription prescription = new Prescription();
        prescription.setTitle((title == null || title.isBlank()) ? "Prescription" : title);
        prescription.setDescription(description);
        prescription.setFilePath(storedPath);
        prescription.setPrescribedDate(LocalDate.now());
        prescription.setDoctor(doctor);
        prescription.setPatient(patient);

        Prescription saved = prescriptionRepository.save(prescription);

        notificationService.notifyUser(
            patientUser,
            "PRESCRIPTION_UPLOADED",
            "New Prescription Available",
            "Dr. " + doctorUser.getName() + " uploaded a new prescription for you.",
            "/patient/prescriptions"
        );

        return saved;
    }

    public List<Prescription> getPatientPrescriptions(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        PatientProfile patient = patientProfileRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Patient profile not found"));

        return prescriptionRepository.findByPatientOrderByPrescribedDateDesc(patient);
    }

    public List<Prescription> getDoctorPrescriptions(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        DoctorProfile doctor = doctorProfileRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Doctor profile not found"));

        return prescriptionRepository.findByDoctorOrderByPrescribedDateDesc(doctor);
    }

    public Prescription submitPrescriptionFeedback(String patientEmail,
                                                   Long prescriptionId,
                                                   Integer rating,
                                                   String feedback) {
        if (rating == null || rating < 1 || rating > 10) {
            throw new RuntimeException("Rating must be between 1 and 10");
        }

        User user = userRepository.findByEmail(patientEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        PatientProfile patient = patientProfileRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Patient profile not found"));

        Prescription prescription = prescriptionRepository.findById(prescriptionId)
                .orElseThrow(() -> new RuntimeException("Prescription not found"));

        if (!prescription.getPatient().getId().equals(patient.getId())) {
            throw new RuntimeException("You are not allowed to submit feedback for this prescription");
        }

        prescription.setFeedbackRating(rating);
        prescription.setFeedbackComment(feedback == null ? null : feedback.trim());
        prescription.setFeedbackDate(LocalDate.now());
        return prescriptionRepository.save(prescription);
    }

    public List<Prescription> getDoctorFeedbacks(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        DoctorProfile doctor = doctorProfileRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Doctor profile not found"));

        return prescriptionRepository.findByDoctorAndFeedbackRatingIsNotNullOrderByFeedbackDateDesc(doctor);
    }

    private String normalizeRecordCategory(String rawCategory) {
        if (rawCategory == null || rawCategory.isBlank()) {
            return "test_reports";
        }

        String normalized = rawCategory.trim()
                .toLowerCase()
                .replace('-', '_')
                .replace(' ', '_');

        if (!ALLOWED_RECORD_CATEGORIES.contains(normalized)) {
            throw new RuntimeException("Invalid record category");
        }

        return normalized;
    }

    private String storeFile(MultipartFile file, String category) {
        try {
            Path uploadDir = Paths.get("uploads", category);
            Files.createDirectories(uploadDir);

            String originalName = (file.getOriginalFilename() == null || file.getOriginalFilename().isBlank())
                    ? "file"
                    : file.getOriginalFilename().replaceAll("[^a-zA-Z0-9._-]", "_");

            String fileName = System.currentTimeMillis() + "_" + originalName;
            Path targetPath = uploadDir.resolve(fileName);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            return "uploads/" + category + "/" + fileName;
        } catch (IOException ex) {
            throw new RuntimeException("Failed to store file", ex);
        }
    }

    private void deleteStoredFile(String relativePath) {
        if (relativePath == null || relativePath.isBlank()) {
            return;
        }

        try {
            Path path = Paths.get(relativePath);
            if (Files.exists(path)) {
                Files.delete(path);
            }
        } catch (IOException ex) {
            // Non-blocking cleanup failure.
        }
    }

    public String createDoctorProfile(String email, DoctorProfileRequest request) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getRole() != Role.DOCTOR) {
            throw new RuntimeException("Only doctors can create profile");
        }

        if (doctorProfileRepository.findByUser(user).isPresent()) {
            throw new RuntimeException("Profile already exists");
        }

        // 🔥 UPDATE USER NAME
        user.setName(request.getName());
        userRepository.save(user);

        DoctorProfile profile = new DoctorProfile();

        profile.setGender(request.getGender());
        profile.setPhoneNumber(request.getPhoneNumber());

        profile.setSpecialization(request.getSpecialization());
        profile.setQualification(request.getQualification());
        profile.setExperience(request.getExperience());
        profile.setConsultationFee(request.getConsultationFee());

        profile.setHospitalName(request.getHospitalName());
        profile.setState(request.getState());
        profile.setCity(request.getCity());

        profile.setAvailableDays(request.getAvailableDays());
        profile.setAvailableTime(request.getAvailableTime());

        profile.setRegistrationNumber(request.getRegistrationNumber());
        profile.setBio(request.getBio());

        profile.setUser(user);

        doctorProfileRepository.save(profile);

        return "Doctor Profile Created Successfully!";
    }
    public PatientProfile getPatientProfile(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return patientProfileRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Profile not found"));
    }
    public DoctorProfile getDoctorProfile(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return doctorProfileRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Doctor profile not found"));
    }
    public DoctorProfile updateDoctorProfile(String email, DoctorProfileRequest request) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        DoctorProfile profile = doctorProfileRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Doctor profile not found"));

        profile.setQualification(request.getQualification());
        profile.setSpecialization(request.getSpecialization());
        profile.setExperience(request.getExperience());
        profile.setConsultationFee(request.getConsultationFee());
        profile.setHospitalName(request.getHospitalName());
        profile.setRegistrationNumber(request.getRegistrationNumber());
        profile.setPhoneNumber(request.getPhoneNumber());
        profile.setGender(request.getGender());
        profile.setBio(request.getBio());

        return doctorProfileRepository.save(profile);
    }
    public Appointment bookAppointment(String patientEmail, AppointmentRequest request) {

        User patientUser = userRepository.findByEmail(patientEmail)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        PatientProfile patient = patientProfileRepository.findByUser(patientUser)
                .orElseThrow(() -> new RuntimeException("Patient profile not found"));

        DoctorProfile doctor = doctorProfileRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new RuntimeException("Doctor not found"));
        boolean exists = appointmentRepository
                .existsByDoctorAndDateAndTime(
                        doctor,
                        request.getDate(),
                        request.getTime()
                );

        if (exists) {
            throw new RuntimeException("This slot is already booked");
        }
        Appointment appointment = new Appointment();
        appointment.setDoctor(doctor);
        appointment.setPatient(patient);
        appointment.setDate(request.getDate());
        appointment.setTime(request.getTime());
        appointment.setStatus("PENDING");
        appointment.setDescription(request.getDescription());
        appointment.setSharedRecordIds(sanitizeRecordIds(request.getRecordIds()));

        List<Long> recordIds = request.getRecordIds();
        if (recordIds != null && !recordIds.isEmpty()) {
            for (Long recordId : recordIds) {
                if (recordId == null) continue;

                medicalRecordRepository.findByIdAndPatient(recordId, patient).ifPresent(record -> {
                    record.setSharedWithDoctors(true);
                    medicalRecordRepository.save(record);
                });
            }
        }

        Appointment saved = appointmentRepository.save(appointment);

        notificationService.notifyUser(
            doctor.getUser(),
            "APPOINTMENT_REQUEST",
            "New Appointment Request",
            "A new appointment request was submitted by " + patientUser.getName() + ".",
            "/doctor/appointments"
        );

        notificationService.notifyUser(
            patientUser,
            "APPOINTMENT_BOOKED",
            "Appointment Requested",
            "Your appointment request has been sent to Dr. " + doctor.getUser().getName() + ".",
            "/patient/appointments"
        );

        return saved;
    }
        public List<Map<String, Object>> getDoctorAppointments(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        DoctorProfile doctor = doctorProfileRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Doctor profile not found"));

        return appointmentRepository.findByDoctor(doctor)
            .stream()
            .sorted(Comparator.comparing(Appointment::getDate).reversed().thenComparing(Appointment::getTime).reversed())
            .map(this::toDoctorAppointmentResponse)
            .collect(Collectors.toList());
    }

            public Map<String, Object> createRecordAccessRequest(String doctorEmail,
                                      Long appointmentId,
                                      DoctorRecordRequestCreateRequest request) {
            if (request == null) {
                throw new RuntimeException("Request payload is required");
            }

            String requestedCategory = request.getRequestedCategory() == null
                ? null
                : request.getRequestedCategory().trim();
            String requestedReportName = request.getRequestedReportName() == null
                ? null
                : request.getRequestedReportName().trim();

            if ((requestedCategory == null || requestedCategory.isBlank())
                && (requestedReportName == null || requestedReportName.isBlank())) {
                throw new RuntimeException("Provide a category or specific report name");
            }

            User doctorUser = userRepository.findByEmail(doctorEmail)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

            DoctorProfile doctor = doctorProfileRepository.findByUser(doctorUser)
                .orElseThrow(() -> new RuntimeException("Doctor profile not found"));

            Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

            if (appointment.getDoctor() == null || appointment.getDoctor().getId() == null
                || !appointment.getDoctor().getId().equals(doctor.getId())) {
                throw new RuntimeException("You can only request reports for your own appointments");
            }

            RecordAccessRequest accessRequest = new RecordAccessRequest();
            accessRequest.setAppointment(appointment);
            accessRequest.setDoctor(doctor);
            accessRequest.setPatient(appointment.getPatient());
            accessRequest.setRequestType((requestedCategory != null && !requestedCategory.isBlank()) ? "CATEGORY" : "SPECIFIC");
            accessRequest.setRequestedCategory(requestedCategory);
            accessRequest.setRequestedReportName(requestedReportName);
            accessRequest.setNote(request.getNote());
            accessRequest.setStatus("PENDING");
            accessRequest.setRequestedAt(java.time.LocalDateTime.now());
            accessRequest.setApprovedRecordIds(new ArrayList<>());

            RecordAccessRequest saved = recordAccessRequestRepository.save(accessRequest);

            notificationService.notifyUser(
                appointment.getPatient().getUser(),
                "RECORD_REQUEST_CREATED",
                "Doctor Requested Records",
                "Dr. " + doctorUser.getName() + " requested access to your medical records.",
                "/patient/requests"
            );

            return toRecordAccessRequestResponse(saved);
            }

            public List<Map<String, Object>> getDoctorRecordAccessRequests(String doctorEmail) {
            User doctorUser = userRepository.findByEmail(doctorEmail)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

            DoctorProfile doctor = doctorProfileRepository.findByUser(doctorUser)
                .orElseThrow(() -> new RuntimeException("Doctor profile not found"));

            return recordAccessRequestRepository.findByDoctorOrderByRequestedAtDesc(doctor)
                .stream()
                .map(this::toRecordAccessRequestResponse)
                .collect(Collectors.toList());
            }

            public List<Map<String, Object>> getPatientRecordAccessRequests(String patientEmail) {
            User patientUser = userRepository.findByEmail(patientEmail)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

            PatientProfile patient = patientProfileRepository.findByUser(patientUser)
                .orElseThrow(() -> new RuntimeException("Patient profile not found"));

            return recordAccessRequestRepository.findByPatientOrderByRequestedAtDesc(patient)
                .stream()
                .map(this::toRecordAccessRequestResponse)
                .collect(Collectors.toList());
            }

            public Map<String, Object> respondToRecordAccessRequest(String patientEmail,
                                        Long requestId,
                                        PatientRecordRequestResponseRequest request) {
            if (request == null) {
                throw new RuntimeException("Response payload is required");
            }

            User patientUser = userRepository.findByEmail(patientEmail)
                .orElseThrow(() -> new RuntimeException("Patient not found"));

            PatientProfile patient = patientProfileRepository.findByUser(patientUser)
                .orElseThrow(() -> new RuntimeException("Patient profile not found"));

            RecordAccessRequest accessRequest = recordAccessRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Record request not found"));

            if (accessRequest.getPatient() == null || accessRequest.getPatient().getId() == null
                || !accessRequest.getPatient().getId().equals(patient.getId())) {
                throw new RuntimeException("You are not allowed to respond to this request");
            }

            if (!"PENDING".equalsIgnoreCase(accessRequest.getStatus())) {
                throw new RuntimeException("This request is already resolved");
            }

            if (request.isApproved()) {
                List<Long> approvedIds = sanitizeRecordIds(request.getRecordIds());
                if (approvedIds.isEmpty()) {
                throw new RuntimeException("Select at least one record to approve");
                }

                List<MedicalRecord> records = medicalRecordRepository.findByPatientAndIdIn(patient, approvedIds);
                List<Long> allowedIds = records.stream().map(MedicalRecord::getId).collect(Collectors.toList());
                if (allowedIds.isEmpty()) {
                throw new RuntimeException("No valid records selected");
                }

                for (MedicalRecord record : records) {
                record.setSharedWithDoctors(true);
                medicalRecordRepository.save(record);
                }

                accessRequest.setStatus("APPROVED");
                accessRequest.setApprovedRecordIds(allowedIds);
            } else {
                accessRequest.setStatus("REJECTED");
                accessRequest.setApprovedRecordIds(new ArrayList<>());
            }

            accessRequest.setRespondedAt(java.time.LocalDateTime.now());
            RecordAccessRequest saved = recordAccessRequestRepository.save(accessRequest);

            String responseAction = "APPROVED".equalsIgnoreCase(saved.getStatus()) ? "approved" : "rejected";
            notificationService.notifyUser(
                accessRequest.getDoctor().getUser(),
                "RECORD_REQUEST_RESPONSE",
                "Record Request " + responseAction.substring(0, 1).toUpperCase() + responseAction.substring(1),
                "Patient " + patientUser.getName() + " " + responseAction + " your record access request.",
                "/doctor/patients"
            );

            return toRecordAccessRequestResponse(saved);
            }

    public Appointment updateAppointmentStatus(Long appointmentId, String status) {

        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        String oldStatus = appointment.getStatus();

        if ("COMPLETED".equalsIgnoreCase(status)) {
            throw new RuntimeException("Use prescription flow to complete an appointment");
        }

        // Block unverified doctors from approving appointments
        if ("APPROVED".equalsIgnoreCase(status) && appointment.getDoctor() != null) {
            DoctorProfile doctor = appointment.getDoctor();
            boolean verified = doctorVerificationRepository.findByDoctorProfile(doctor)
                    .map(v -> "VERIFIED".equals(v.getStatus()))
                    .orElse(false);
            if (!verified) {
                throw new RuntimeException("Your profile is not verified. Please submit a verification request and wait for admin approval before accepting appointments.");
            }
        }

        appointment.setStatus(status);

        Appointment saved = appointmentRepository.save(appointment);

        if (oldStatus == null || !oldStatus.equalsIgnoreCase(status)) {
            notificationService.notifyUser(
                    appointment.getPatient().getUser(),
                    "APPOINTMENT_STATUS_UPDATED",
                    "Appointment " + status,
                    "Your appointment with Dr. " + appointment.getDoctor().getUser().getName() + " is now " + status + ".",
                    "/patient/appointments"
            );
        }

        return saved;
    }

    // ===== Doctor Verification =====

    public Map<String, Object> submitVerification(String email, DoctorVerificationRequest request, MultipartFile certificateFile) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        DoctorProfile doctor = doctorProfileRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Doctor profile not found"));

        DoctorVerification verification = doctorVerificationRepository
                .findByDoctorProfile(doctor)
                .orElse(new DoctorVerification());

        verification.setDoctorProfile(doctor);
        verification.setQualificationDetails(request.getQualificationDetails());
        verification.setPracticeDetails(request.getPracticeDetails());
        verification.setCertificateInfo(request.getCertificateInfo());
        if (certificateFile != null && !certificateFile.isEmpty()) {
            verification.setCertificateFilePath(storeFile(certificateFile, "certificates"));
        }
        verification.setRegistrationNumber(request.getRegistrationNumber());
        verification.setAdditionalInfo(request.getAdditionalInfo());
        verification.setStatus("PENDING");
        verification.setRejectionReason(null);
        verification.setSubmittedAt(java.time.LocalDateTime.now());
        verification.setReviewedAt(null);

        doctorVerificationRepository.save(verification);

        notificationService.notifyRole(
            Role.ADMIN,
            "DOCTOR_VERIFICATION_SUBMITTED",
            "New Doctor Verification Request",
            "Dr. " + user.getName() + " submitted a verification request.",
            "/admin/doctors"
        );

        notificationService.notifyUser(
            user,
            "DOCTOR_VERIFICATION_SUBMITTED",
            "Verification Submitted",
            "Your verification request was submitted and is pending admin review.",
            "/doctor/profile"
        );

        Map<String, Object> resp = new HashMap<>();
        resp.put("message", "Verification request submitted successfully");
        resp.put("status", "PENDING");
        return resp;
    }

    public Map<String, Object> getVerificationStatus(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        DoctorProfile doctor = doctorProfileRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Doctor profile not found"));

        return doctorVerificationRepository.findByDoctorProfile(doctor)
                .map(v -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("status", v.getStatus());
                    m.put("rejectionReason", v.getRejectionReason());
                    m.put("submittedAt", v.getSubmittedAt());
                    m.put("reviewedAt", v.getReviewedAt());
                    m.put("qualificationDetails", v.getQualificationDetails());
                    m.put("practiceDetails", v.getPracticeDetails());
                    m.put("certificateInfo", v.getCertificateInfo());
                    m.put("certificateFilePath", v.getCertificateFilePath());
                    m.put("registrationNumber", v.getRegistrationNumber());
                    m.put("additionalInfo", v.getAdditionalInfo());
                    return m;
                })
                .orElse(null);
    }

    public Map<String, Object> completeAppointmentWithPrescription(
            Long appointmentId,
            CompleteAppointmentPrescriptionRequest request) {

        if (request == null || request.getDoctorEmail() == null || request.getDoctorEmail().isBlank()) {
            throw new RuntimeException("Doctor email is required");
        }

        User doctorUser = userRepository.findByEmail(request.getDoctorEmail())
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        DoctorProfile doctor = doctorProfileRepository.findByUser(doctorUser)
                .orElseThrow(() -> new RuntimeException("Doctor profile not found"));

        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        if (appointment.getDoctor() == null || appointment.getDoctor().getId() == null
                || !appointment.getDoctor().getId().equals(doctor.getId())) {
            throw new RuntimeException("This appointment does not belong to the doctor");
        }

        if (!"APPROVED".equalsIgnoreCase(appointment.getStatus())) {
            throw new RuntimeException("Only approved appointments can be completed");
        }

        if (prescriptionRepository.existsByAppointment(appointment)) {
            throw new RuntimeException("Prescription already exists for this appointment");
        }

        Prescription prescription = new Prescription();
        prescription.setTitle((request.getTitle() == null || request.getTitle().isBlank())
                ? "Digital Prescription - Appointment #" + appointment.getId()
                : request.getTitle().trim());
        prescription.setDescription(buildDigitalPrescriptionText(appointment, doctor, request));
        prescription.setFilePath(null);
        prescription.setPrescribedDate(LocalDate.now());
        prescription.setDoctor(doctor);
        prescription.setPatient(appointment.getPatient());
        prescription.setAppointment(appointment);

        Prescription saved = prescriptionRepository.save(prescription);

        appointment.setStatus("COMPLETED");
        appointmentRepository.save(appointment);

        notificationService.notifyUser(
            appointment.getPatient().getUser(),
            "APPOINTMENT_COMPLETED",
            "Appointment Completed",
            "Your appointment with Dr. " + doctorUser.getName() + " is completed. A new prescription is available.",
            "/patient/prescriptions"
        );

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Prescription created and appointment marked as COMPLETED");
        response.put("appointmentId", appointment.getId());
        response.put("appointmentStatus", appointment.getStatus());
        response.put("prescription", saved);
        return response;
    }
    public List<Appointment> getPatientAppointments(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        PatientProfile patient = patientProfileRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Patient profile not found"));

        return appointmentRepository.findByPatient(patient);
    }

    private List<Long> sanitizeRecordIds(List<Long> recordIds) {
        if (recordIds == null || recordIds.isEmpty()) {
            return new ArrayList<>();
        }

        return recordIds.stream()
                .filter(id -> id != null && id > 0)
                .collect(Collectors.toCollection(LinkedHashSet::new))
                .stream()
                .collect(Collectors.toList());
    }

    private List<MedicalRecord> resolveSharedRecords(Appointment appointment) {
        PatientProfile patient = appointment.getPatient();
        if (patient == null) {
            return Collections.emptyList();
        }

        List<Long> mergedIds = new ArrayList<>();
        mergedIds.addAll(sanitizeRecordIds(appointment.getSharedRecordIds()));

        List<RecordAccessRequest> approvedRequests = recordAccessRequestRepository
                .findByAppointmentAndStatus(appointment, "APPROVED");
        for (RecordAccessRequest request : approvedRequests) {
            mergedIds.addAll(sanitizeRecordIds(request.getApprovedRecordIds()));
        }

        mergedIds = sanitizeRecordIds(mergedIds);

        if (!mergedIds.isEmpty()) {
            return medicalRecordRepository.findByPatientAndIdIn(patient, mergedIds)
                    .stream()
                    .sorted(Comparator.comparing(MedicalRecord::getUploadedDate, Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                    .collect(Collectors.toList());
        }

        return Collections.emptyList();
    }

    private Map<String, Object> toDoctorAppointmentResponse(Appointment appointment) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", appointment.getId());
        response.put("doctor", appointment.getDoctor());
        response.put("patient", appointment.getPatient());
        response.put("date", appointment.getDate());
        response.put("time", appointment.getTime());
        response.put("status", appointment.getStatus());
        response.put("description", appointment.getDescription());
        response.put("sharedRecordIds", appointment.getSharedRecordIds());
        response.put("sharedRecords", resolveSharedRecords(appointment));
        response.put("appointmentPrescription", prescriptionRepository.findByAppointment(appointment)
                .map(this::toAppointmentPrescriptionSummary)
                .orElse(null));
        return response;
    }

    private Map<String, Object> toAppointmentPrescriptionSummary(Prescription prescription) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", prescription.getId());
        response.put("title", prescription.getTitle());
        response.put("description", prescription.getDescription());
        response.put("filePath", prescription.getFilePath());
        response.put("prescribedDate", prescription.getPrescribedDate());
        return response;
    }

    private Map<String, Object> toRecordAccessRequestResponse(RecordAccessRequest request) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", request.getId());
        response.put("requestType", request.getRequestType());
        response.put("requestedCategory", request.getRequestedCategory());
        response.put("requestedReportName", request.getRequestedReportName());
        response.put("note", request.getNote());
        response.put("status", request.getStatus());
        response.put("requestedAt", request.getRequestedAt());
        response.put("respondedAt", request.getRespondedAt());
        response.put("approvedRecordIds", sanitizeRecordIds(request.getApprovedRecordIds()));
        response.put("appointment", request.getAppointment());
        response.put("doctor", request.getDoctor());
        response.put("patient", request.getPatient());

        PatientProfile patient = request.getPatient();
        List<Long> approvedIds = sanitizeRecordIds(request.getApprovedRecordIds());
        if (patient != null && !approvedIds.isEmpty()) {
            response.put("approvedRecords", medicalRecordRepository.findByPatientAndIdIn(patient, approvedIds));
        } else {
            response.put("approvedRecords", Collections.emptyList());
        }

        return response;
    }

    private String buildDigitalPrescriptionText(
            Appointment appointment,
            DoctorProfile doctor,
            CompleteAppointmentPrescriptionRequest request) {

        PatientProfile patient = appointment.getPatient();
        User doctorUser = doctor.getUser();

        String hospitalName = safe(doctor.getHospitalName(), "MEDVAULT AFFILIATED HOSPITAL");
        String doctorName = safe(doctorUser != null ? doctorUser.getName() : null, "N/A");
        String qualification = safe(doctor.getQualification(), "N/A");
        String regNo = safe(doctor.getRegistrationNumber(), "N/A");
        String department = safe(doctor.getSpecialization(), "General Medicine");

        String patientName = safe(patient != null ? patient.getName() : null, "N/A");
        String patientId = patient != null && patient.getId() != null ? String.valueOf(patient.getId()) : "N/A";
        String ageGender = patient == null
                ? "N/A"
                : safe((patient.getAge() != null ? patient.getAge() : "-") + " / " + safe(patient.getGender(), "-"), "N/A");
        String contact = safe(patient != null ? patient.getPhoneNumber() : null, "N/A");
        String address = safe(formatAddress(doctor), "N/A");

        StringBuilder sb = new StringBuilder();
        sb.append("====================================================================\n");
        sb.append("                         [").append(hospitalName).append("]\n");
        sb.append("                     Digital Medical Prescription\n");
        sb.append("====================================================================\n\n");
        sb.append("Doctor Name      : ").append(doctorName).append("\n");
        sb.append("Qualification    : ").append(qualification).append("\n");
        sb.append("Medical Reg No   : ").append(regNo).append("\n");
        sb.append("Department       : ").append(department).append("\n");
        sb.append("Date             : ").append(LocalDate.now()).append("\n");
        sb.append("Time             : ").append(LocalTime.now().withSecond(0).withNano(0)).append("\n\n");
        sb.append("--------------------------------------------------------------------\n\n");
        sb.append("Patient Name     : ").append(patientName).append("\n");
        sb.append("Patient ID       : ").append(patientId).append("\n");
        sb.append("Age / Gender     : ").append(ageGender).append("\n");
        sb.append("Contact Number   : ").append(contact).append("\n");
        sb.append("Address          : ").append(address).append("\n\n");
        sb.append("--------------------------------------------------------------------\n\n");
        sb.append("Chief Complaint:\n");
        sb.append(nonEmptyOrLine(request.getChiefComplaint())).append("\n\n");
        sb.append("--------------------------------------------------------------------\n\n");
        sb.append("Diagnosis:\n");
        sb.append(nonEmptyOrLine(request.getDiagnosis())).append("\n\n");
        sb.append("--------------------------------------------------------------------\n\n");
        sb.append("Prescription (Rx):\n\n");

        List<CompleteAppointmentPrescriptionRequest.MedicineItem> medicines =
                request.getMedicines() == null ? new ArrayList<>() : request.getMedicines();

        for (int i = 0; i < 4; i++) {
            CompleteAppointmentPrescriptionRequest.MedicineItem med = i < medicines.size() ? medicines.get(i) : null;
            sb.append(i + 1).append(". Medicine Name : ").append(safe(med != null ? med.getName() : null, "-")).append("\n");
            sb.append("   Dosage        : ").append(safe(med != null ? med.getDosage() : null, "-")).append("\n");
            sb.append("   Frequency     : ").append(safe(med != null ? med.getFrequency() : null, "-")).append("\n");
            sb.append("   Duration      : ").append(safe(med != null ? med.getDuration() : null, "-")).append("\n");
            sb.append("   Instructions  : ").append(safe(med != null ? med.getInstructions() : null, "-")).append("\n\n");
        }

        sb.append("--------------------------------------------------------------------\n\n");
        sb.append("Recommended Tests:\n");
        sb.append(formatBullets(request.getRecommendedTests())).append("\n\n");
        sb.append("--------------------------------------------------------------------\n\n");
        sb.append("Doctor Advice:\n");
        sb.append(formatBullets(request.getDoctorAdvice())).append("\n\n");
        sb.append("--------------------------------------------------------------------\n\n");
        sb.append("Follow-up Date : ").append(safe(request.getFollowUpDate(), "-")).append("\n\n");
        sb.append("--------------------------------------------------------------------\n\n");
        sb.append("Doctor Signature : ").append(doctorName).append("\n\n");
        sb.append("(Computer Generated Digital Prescription)\n\n");
        sb.append("====================================================================");

        return sb.toString();
    }

    private String formatBullets(String raw) {
        if (raw == null || raw.isBlank()) {
            return "• _________________________________________________\n"
                    + "• _________________________________________________\n"
                    + "• _________________________________________________";
        }

        String[] lines = raw.split("\\r?\\n");
        List<String> cleaned = new ArrayList<>();
        for (String line : lines) {
            if (line != null && !line.trim().isEmpty()) {
                cleaned.add("• " + line.trim());
            }
        }

        if (cleaned.isEmpty()) {
            return "• _________________________________________________\n"
                    + "• _________________________________________________\n"
                    + "• _________________________________________________";
        }

        return String.join("\n", cleaned);
    }

    private String nonEmptyOrLine(String value) {
        if (value == null || value.isBlank()) {
            return "____________________________________________________________";
        }
        return value.trim();
    }

    private String formatAddress(DoctorProfile doctor) {
        String city = doctor == null ? null : doctor.getCity();
        String state = doctor == null ? null : doctor.getState();
        if ((city == null || city.isBlank()) && (state == null || state.isBlank())) {
            return "N/A";
        }
        if (city == null || city.isBlank()) {
            return state;
        }
        if (state == null || state.isBlank()) {
            return city;
        }
        return city + ", " + state;
    }

    private String safe(String value, String fallback) {
        return (value == null || value.isBlank()) ? fallback : value.trim();
    }

    public String detectSpecialization(String description) {

        if (description == null || description.isBlank()) {
            return "General Physician";
        }

        String normalized = description
                .toLowerCase()
                .replaceAll("[^a-z\\s]", " ")
                .replaceAll("\\s+", " ")
                .trim();

        if (normalized.isEmpty()) {
            return "General Physician";
        }

        Set<String> tokens = Set.of(normalized.split(" "));
        boolean hasSpecificKeyword = false;
        int bestScore = 0;
        String bestSpecialization = "General Physician";

        for (Map.Entry<String, List<String>> entry : SPECIALIZATION_KEYWORDS.entrySet()) {
            int score = 0;
            for (String keyword : entry.getValue()) {
                if (tokens.contains(keyword) || normalized.contains(keyword)) {
                    score++;
                    hasSpecificKeyword = true;
                }
            }

            if (score > bestScore) {
                bestScore = score;
                bestSpecialization = entry.getKey();
            }
        }

        if (!hasSpecificKeyword) {
            return "General Physician";
        }

        int genericHits = 0;
        for (String token : tokens) {
            if (GENERIC_SYMPTOMS.contains(token)) {
                genericHits++;
            }
        }

        // If text is mostly generic symptoms with weak specialty signal, fallback.
        if (bestScore == 1 && genericHits >= 2) {
            return "General Physician";
        }

        return bestSpecialization;
    }
    public List<DoctorProfile> recommendDoctors(String description){
        return doctorProfileRepository.findAll();
    }

    public List<AvailableSlotResponse> getAvailableSlots(Long doctorId, LocalDate date) {

        DoctorProfile doctor = doctorProfileRepository
                .findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        // Check doctor working day with robust parsing for ranges like Mon-Fri.
        Set<String> workingDays = parseWorkingDays(doctor.getAvailableDays());
        String day = date.getDayOfWeek().name().toLowerCase().substring(0, 3);

        if (!workingDays.isEmpty() && !workingDays.contains(day)) {
            throw new RuntimeException("Doctor not available this day");
        }

        // Parse time range formats like 10-14, 10AM-2PM, 10:30 - 14:00.
        LocalTime[] timeRange = parseTimeRange(doctor.getAvailableTime());
        LocalTime start = timeRange[0];
        LocalTime end = timeRange[1];

        List<AvailableSlotResponse> slots = new ArrayList<>();

        while (start.isBefore(end)) {

            boolean booked = appointmentRepository
                    .existsByDoctorAndDateAndTime(doctor, date, start);

            slots.add(new AvailableSlotResponse(start, !booked));

            start = start.plusMinutes(30);
        }

        return slots;
    }

    private Set<String> parseWorkingDays(String availableDays) {
        if (availableDays == null || availableDays.isBlank()) {
            return Collections.emptySet();
        }

        List<String> dayOrder = List.of("sun", "mon", "tue", "wed", "thu", "fri", "sat");
        String normalized = availableDays
                .toLowerCase()
                .replace("monday", "mon")
                .replace("tuesday", "tue")
                .replace("wednesday", "wed")
                .replace("thursday", "thu")
                .replace("friday", "fri")
                .replace("saturday", "sat")
                .replace("sunday", "sun")
                .replace("–", "-")
                .replace("—", "-")
                .replaceAll("\\s+", "");

        Set<String> result = new HashSet<>();

        for (String part : normalized.split(",")) {
            if (part.isBlank()) {
                continue;
            }

            if (part.contains("-")) {
                String[] range = part.split("-");
                if (range.length != 2) {
                    continue;
                }

                String start = range[0].length() >= 3 ? range[0].substring(0, 3) : range[0];
                String end = range[1].length() >= 3 ? range[1].substring(0, 3) : range[1];

                int startIdx = dayOrder.indexOf(start);
                int endIdx = dayOrder.indexOf(end);
                if (startIdx == -1 || endIdx == -1) {
                    continue;
                }

                if (startIdx <= endIdx) {
                    for (int i = startIdx; i <= endIdx; i++) {
                        result.add(dayOrder.get(i));
                    }
                } else {
                    for (int i = startIdx; i < dayOrder.size(); i++) {
                        result.add(dayOrder.get(i));
                    }
                    for (int i = 0; i <= endIdx; i++) {
                        result.add(dayOrder.get(i));
                    }
                }
            } else {
                String day = part.length() >= 3 ? part.substring(0, 3) : part;
                if (dayOrder.contains(day)) {
                    result.add(day);
                }
            }
        }

        return result;
    }

    private LocalTime[] parseTimeRange(String availableTime) {
        if (availableTime == null || availableTime.isBlank()) {
            throw new RuntimeException("Doctor available time is not configured");
        }

        String normalized = availableTime.trim().toLowerCase().replace("–", "-").replace("—", "-");
        String[] parts = normalized.split("\\s*(?:-|to)\\s*");

        if (parts.length != 2) {
            throw new RuntimeException("Invalid doctor available time format");
        }

        LocalTime start = parseSingleTime(parts[0]);
        LocalTime end = parseSingleTime(parts[1]);

        if (!start.isBefore(end)) {
            throw new RuntimeException("Invalid doctor available time range");
        }

        return new LocalTime[] { start, end };
    }

    private LocalTime parseSingleTime(String rawValue) {
        String value = rawValue.trim().toLowerCase().replaceAll("\\s+", "");

        boolean isAm = value.endsWith("am");
        boolean isPm = value.endsWith("pm");
        if (isAm || isPm) {
            value = value.substring(0, value.length() - 2);
        }

        int hour;
        int minute = 0;

        if (value.contains(":")) {
            String[] hm = value.split(":");
            if (hm.length != 2) {
                throw new RuntimeException("Invalid time value");
            }
            hour = Integer.parseInt(hm[0]);
            minute = Integer.parseInt(hm[1]);
        } else {
            hour = Integer.parseInt(value.replaceAll("[^0-9]", ""));
        }

        if (isPm && hour < 12) {
            hour += 12;
        }
        if (isAm && hour == 12) {
            hour = 0;
        }

        return LocalTime.of(hour, minute);
    }
}