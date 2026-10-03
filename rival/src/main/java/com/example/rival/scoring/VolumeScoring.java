package com.example.rival.scoring;

import com.example.rival.model.ScoringType;
import com.example.rival.model.WorkoutLog;
import org.springframework.stereotype.Component;

import java.util.List;

// Raw weight moved. Favours the stronger lifter.
@Component
public class VolumeScoring implements ScoringStrategy {

    @Override
    public ScoringType type() {
        return ScoringType.VOLUME;
    }

    @Override
    public long score(List<WorkoutLog> thisWeek, List<WorkoutLog> previousFour) {
        return ScoringStrategy.totalVolume(thisWeek) / 100;
    }
}
