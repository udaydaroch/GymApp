package com.example.rival.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "set_entries")
public class SetEntry {

    // Bodyweight sets have no weight, so they count as this many kg when scoring
    public static final double BODYWEIGHT_KG = 10;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "workout_log_id")
    private WorkoutLog workoutLog;

    @ManyToOne(optional = false)
    @JoinColumn(name = "exercise_id")
    private Exercise exercise;

    private int reps;

    // Kilograms; 0 means bodyweight
    private double weight;

    public SetEntry() {
    }

    public SetEntry(Exercise exercise, int reps, double weight) {
        this.exercise = exercise;
        this.reps = reps;
        this.weight = weight;
    }

    public double getVolume() {
        return reps * (weight > 0 ? weight : BODYWEIGHT_KG);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public WorkoutLog getWorkoutLog() { return workoutLog; }
    public void setWorkoutLog(WorkoutLog workoutLog) { this.workoutLog = workoutLog; }

    public Exercise getExercise() { return exercise; }
    public void setExercise(Exercise exercise) { this.exercise = exercise; }

    public int getReps() { return reps; }
    public void setReps(int reps) { this.reps = reps; }

    public double getWeight() { return weight; }
    public void setWeight(double weight) { this.weight = weight; }
}
