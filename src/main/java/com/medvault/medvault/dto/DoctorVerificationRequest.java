package com.medvault.medvault.dto;

public class DoctorVerificationRequest {
    private String qualificationDetails;
    private String practiceDetails;
    private String certificateInfo;
    private String registrationNumber;
    private String additionalInfo;

    public String getQualificationDetails() { return qualificationDetails; }
    public void setQualificationDetails(String qualificationDetails) { this.qualificationDetails = qualificationDetails; }

    public String getPracticeDetails() { return practiceDetails; }
    public void setPracticeDetails(String practiceDetails) { this.practiceDetails = practiceDetails; }

    public String getCertificateInfo() { return certificateInfo; }
    public void setCertificateInfo(String certificateInfo) { this.certificateInfo = certificateInfo; }

    public String getRegistrationNumber() { return registrationNumber; }
    public void setRegistrationNumber(String registrationNumber) { this.registrationNumber = registrationNumber; }

    public String getAdditionalInfo() { return additionalInfo; }
    public void setAdditionalInfo(String additionalInfo) { this.additionalInfo = additionalInfo; }
}
