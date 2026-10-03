package com.example.rival.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

// One line of a routine: "3 sets of 10 squats"
@Entity
@Table(name = "routine_exercises")
public class RoutineExercise implements Prototype<RoutineExercise> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "routine_id")
    private Routine routine;

    @ManyToOne(optional = false)
    @JoinColumn(name = "exercise_id")
    private Exercise exercise;

    @Column(name = "set_count")
    private int sets;

    private int reps;

    public RoutineExercise() {
    }

    public RoutineExercise(Exercise exercise, int sets, int reps) {
        this.exercise = exercise;
        this.sets = sets;
        this.reps = reps;
    }

    // Same exercise, sets and reps, but a new row that belongs to no routine yet
    @Override
    public RoutineExercise copy() {
        return new RoutineExercise(exercise, sets, reps);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Routine getRoutine() { return routine; }
    public void setRoutine(Routine routine) { this.routine = routine; }

    public Exercise getExercise() { return exercise; }
    public void setExercise(Exercise exercise) { this.exercise = exercise; }

    public int getSets() { return sets; }
    public void setSets(int sets) { this.sets = sets; }

    public int getReps() { return reps; }
    public void setReps(int reps) { this.reps = reps; }
}
