package com.example.rival.event;

import com.example.rival.model.WorkoutLog;

// Published by WorkoutService every time a workout is saved
public class WorkoutLoggedEvent {

    private final WorkoutLog workoutLog;

    public WorkoutLoggedEvent(WorkoutLog workoutLog) {
        this.workoutLog = workoutLog;
    }

    public WorkoutLog getWorkoutLog() {
        return workoutLog;
    }
}
