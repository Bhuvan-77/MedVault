package com.medvault.medvault.dto;

import java.util.List;

public class PatientRecordRequestResponseRequest {

    private boolean approved;
    private List<Long> recordIds;

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public List<Long> getRecordIds() {
        return recordIds;
    }

    public void setRecordIds(List<Long> recordIds) {
        this.recordIds = recordIds;
    }
}
