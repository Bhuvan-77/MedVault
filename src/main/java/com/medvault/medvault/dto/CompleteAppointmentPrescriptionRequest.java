package com.medvault.medvault.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CompleteAppointmentPrescriptionRequest {

    private String doctorEmail;
    private String title;

    private String chiefComplaint;
    private String diagnosis;

    private List<MedicineItem> medicines = new ArrayList<>();

    private String recommendedTests;
    private String doctorAdvice;
    private String followUpDate;

    @Getter
    @Setter
    public static class MedicineItem {
        private String name;
        private String dosage;
        private String frequency;
        private String duration;
        private String instructions;
    }
}