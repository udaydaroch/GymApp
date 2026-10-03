package com.example.rival.dto;

import com.example.rival.model.AppUser;
import com.example.rival.model.ScoringType;
import com.example.rival.model.WeeklyBattle;

import java.time.LocalDate;
import java.util.List;

// Everything the rival page needs, seen from one user's side
public class BattleView {

    private final AppUser opponent;
    private final long myScore;
    private final long theirScore;
    private final ScoringType scoringType;
    private final LocalDate weekStart;
    private final LocalDate weekEnd;
    private final List<WeeklyBattle> pastBattles;
    private final int myWins;
    private final int theirWins;

    public BattleView(AppUser opponent, long myScore, long theirScore, ScoringType scoringType,
                      LocalDate weekStart, LocalDate weekEnd, List<WeeklyBattle> pastBattles,
                      int myWins, int theirWins) {
        this.opponent = opponent;
        this.myScore = myScore;
        this.theirScore = theirScore;
        this.scoringType = scoringType;
        this.weekStart = weekStart;
        this.weekEnd = weekEnd;
        this.pastBattles = pastBattles;
        this.myWins = myWins;
        this.theirWins = theirWins;
    }

    public String getStatus() {
        if (myScore > theirScore) {
            return "You're ahead";
        }
        if (myScore < theirScore) {
            return "You're behind";
        }
        return "It's level";
    }

    public AppUser getOpponent() { return opponent; }
    public long getMyScore() { return myScore; }
    public long getTheirScore() { return theirScore; }
    public ScoringType getScoringType() { return scoringType; }
    public LocalDate getWeekStart() { return weekStart; }
    public LocalDate getWeekEnd() { return weekEnd; }
    public List<WeeklyBattle> getPastBattles() { return pastBattles; }
    public int getMyWins() { return myWins; }
    public int getTheirWins() { return theirWins; }
}
