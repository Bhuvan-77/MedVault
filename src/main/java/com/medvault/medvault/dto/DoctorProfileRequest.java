package com.medvault.medvault.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DoctorProfileRequest {
    private String name;
    private String gender;
    private String phoneNumber;

    private String specialization;
    private String qualification;
    private Integer experience;
    private Double consultationFee;

    private String hospitalName;
    private String state;
    private String city;

    private String availableDays;
    private String availableTime;

    private String registrationNumber;
    private String bio;
}
