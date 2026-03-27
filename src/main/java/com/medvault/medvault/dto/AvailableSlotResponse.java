package com.medvault.medvault.dto;

import java.time.LocalTime;

public class AvailableSlotResponse {

    private LocalTime time;
    private boolean available;

    public AvailableSlotResponse(LocalTime time, boolean available) {
        this.time = time;
        this.available = available;
    }

    public LocalTime getTime() {
        return time;
    }

    public boolean isAvailable() {
        return available;
    }
}
