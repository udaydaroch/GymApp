package com.example.rival.scoring;

import com.example.rival.model.ScoringType;
import com.example.rival.model.WorkoutLog;

import java.util.List;

// STRATEGY PATTERN: each way of scoring a weekly battle is its own class.
// To add a new battle type, add a ScoringType and one more class like these.
public interface ScoringStrategy {

    ScoringType type();

    /**
     * @param thisWeek     the user's workouts in the battle week
     * @param previousFour the user's workouts in the 4 weeks before it
     */
    long score(List<WorkoutLog> thisWeek, List<WorkoutLog> previousFour);

    static long totalVolume(List<WorkoutLog> logs) {
        long total = 0;
        for (WorkoutLog log : logs) {
            total += log.getTotalVolume();
        }
        return total;
    }
}
