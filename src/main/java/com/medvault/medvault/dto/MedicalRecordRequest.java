package com.medvault.medvault.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MedicalRecordRequest {

    private String diagnosis;
    private String prescription;
    private String doctorNotes;
    private String visitDate; // we'll convert later
}
