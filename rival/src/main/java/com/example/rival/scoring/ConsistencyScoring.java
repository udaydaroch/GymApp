package com.example.rival.scoring;

import com.example.rival.model.ScoringType;
import com.example.rival.model.WorkoutLog;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// 10 points for every different day trained
@Component
public class ConsistencyScoring implements ScoringStrategy {

    @Override
    public ScoringType type() {
        return ScoringType.CONSISTENCY;
    }

    @Override
    public long score(List<WorkoutLog> thisWeek, List<WorkoutLog> previousFour) {
        Set<LocalDate> days = new HashSet<>();
        for (WorkoutLog log : thisWeek) {
            days.add(log.getWorkoutDate());
        }
        return days.size() * 10L;
    }
}
