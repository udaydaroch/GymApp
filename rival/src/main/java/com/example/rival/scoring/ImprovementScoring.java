package com.example.rival.scoring;

import com.example.rival.model.ScoringType;
import com.example.rival.model.WorkoutLog;
import org.springframework.stereotype.Component;

import java.util.List;

// Scores you against yourself: 100 means "the same as my usual week".
@Component
public class ImprovementScoring implements ScoringStrategy {

    @Override
    public ScoringType type() {
        return ScoringType.IMPROVEMENT;
    }

    @Override
    public long score(List<WorkoutLog> thisWeek, List<WorkoutLog> previousFour) {
        long volume = ScoringStrategy.totalVolume(thisWeek);
        double usualWeek = ScoringStrategy.totalVolume(previousFour) / 4.0;
        if (usualWeek <= 0) {
            // No history yet: any training at all counts as a normal week
            return volume > 0 ? 100 : 0;
        }
        return Math.round(volume / usualWeek * 100);
    }
}
