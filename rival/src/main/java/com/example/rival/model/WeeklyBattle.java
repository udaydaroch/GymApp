package com.example.rival.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDate;

// One Monday-to-Sunday contest between the two users of a rivalry
@Entity
@Table(name = "weekly_battles")
public class WeeklyBattle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "rivalry_id")
    private Rivalry rivalry;

    // Always a Monday
    @Column(nullable = false)
    private LocalDate weekStart;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ScoringType scoringType;

    // Final scores, filled in when the week is closed
    private long scoreA;
    private long scoreB;

    private boolean closed;

    // Empty while open, and when the week ended in a draw
    @ManyToOne
    @JoinColumn(name = "winner_id")
    private AppUser winner;

    public WeeklyBattle() {
    }

    public WeeklyBattle(Rivalry rivalry, LocalDate weekStart, ScoringType scoringType) {
        this.rivalry = rivalry;
        this.weekStart = weekStart;
        this.scoringType = scoringType;
    }

    public LocalDate getWeekEnd() {
        return weekStart.plusDays(6);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Rivalry getRivalry() { return rivalry; }
    public void setRivalry(Rivalry rivalry) { this.rivalry = rivalry; }

    public LocalDate getWeekStart() { return weekStart; }
    public void setWeekStart(LocalDate weekStart) { this.weekStart = weekStart; }

    public ScoringType getScoringType() { return scoringType; }
    public void setScoringType(ScoringType scoringType) { this.scoringType = scoringType; }

    public long getScoreA() { return scoreA; }
    public void setScoreA(long scoreA) { this.scoreA = scoreA; }

    public long getScoreB() { return scoreB; }
    public void setScoreB(long scoreB) { this.scoreB = scoreB; }

    public boolean isClosed() { return closed; }
    public void setClosed(boolean closed) { this.closed = closed; }

    public AppUser getWinner() { return winner; }
    public void setWinner(AppUser winner) { this.winner = winner; }
}
