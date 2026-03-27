package com.medvault.medvault.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PrescriptionFeedbackRequest {
    private Integer rating;
    private String feedback;
}
