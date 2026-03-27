package com.medvault.medvault.dto;

public class DoctorRecordRequestCreateRequest {

    private String requestedCategory;
    private String requestedReportName;
    private String note;

    public String getRequestedCategory() {
        return requestedCategory;
    }

    public void setRequestedCategory(String requestedCategory) {
        this.requestedCategory = requestedCategory;
    }

    public String getRequestedReportName() {
        return requestedReportName;
    }

    public void setRequestedReportName(String requestedReportName) {
        this.requestedReportName = requestedReportName;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
