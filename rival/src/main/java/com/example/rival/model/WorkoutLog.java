package com.example.rival.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "workout_logs")
public class WorkoutLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private AppUser user;

    @Column(nullable = false)
    private LocalDate workoutDate;

    private int durationMinutes;

    // How the member felt, 1 (drained) to 5 (great)
    private int energy;

    // true = shown on the shared feed
    @Column(name = "is_posted")
    private boolean posted = true;

    // Saving or deleting a log also saves or deletes its sets
    @OneToMany(mappedBy = "workoutLog", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SetEntry> sets = new ArrayList<>();

    public WorkoutLog() {
    }

    public WorkoutLog(AppUser user, LocalDate workoutDate, int durationMinutes, int energy) {
        this.user = user;
        this.workoutDate = workoutDate;
        this.durationMinutes = durationMinutes;
        this.energy = energy;
    }

    public void addSet(SetEntry set) {
        set.setWorkoutLog(this);
        sets.add(set);
    }

    // Total weight moved in this workout
    public long getTotalVolume() {
        double total = 0;
        for (SetEntry set : sets) {
            total += set.getVolume();
        }
        return Math.round(total);
    }

    public int getSetCount() {
        return sets.size();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public AppUser getUser() { return user; }
    public void setUser(AppUser user) { this.user = user; }

    public LocalDate getWorkoutDate() { return workoutDate; }
    public void setWorkoutDate(LocalDate workoutDate) { this.workoutDate = workoutDate; }

    public int getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }

    public int getEnergy() { return energy; }
    public void setEnergy(int energy) { this.energy = energy; }

    public boolean isPosted() { return posted; }
    public void setPosted(boolean posted) { this.posted = posted; }

    public List<SetEntry> getSets() { return sets; }
    public void setSets(List<SetEntry> sets) { this.sets = sets; }
}
