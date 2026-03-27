package com.medvault.medvault.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HealthRecordRequest {
    private Double heightCm;
    private Double weightKg;
    private Integer systolicBp;
    private Integer diastolicBp;
    private Integer heartRate;
    private Double bloodSugar;
}
