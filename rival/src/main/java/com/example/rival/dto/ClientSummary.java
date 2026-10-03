package com.example.rival.dto;

import com.example.rival.model.AppUser;

// One row of the trainer dashboard
public class ClientSummary {

    private final AppUser client;
    private final int workoutsThisWeek;
    private final Long daysSinceLastWorkout; // null = never trained
    private final String status;             // "On track", "Slipping" or "At risk"

    public ClientSummary(AppUser client, int workoutsThisWeek, Long daysSinceLastWorkout, String status) {
        this.client = client;
        this.workoutsThisWeek = workoutsThisWeek;
        this.daysSinceLastWorkout = daysSinceLastWorkout;
        this.status = status;
    }

    public AppUser getClient() { return client; }
    public int getWorkoutsThisWeek() { return workoutsThisWeek; }
    public Long getDaysSinceLastWorkout() { return daysSinceLastWorkout; }
    public String getStatus() { return status; }
}
