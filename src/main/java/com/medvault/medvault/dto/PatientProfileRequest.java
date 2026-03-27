package com.medvault.medvault.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PatientProfileRequest {

    private String name;
    private Integer age;
    private String gender;
    private String phoneNumber;
}
