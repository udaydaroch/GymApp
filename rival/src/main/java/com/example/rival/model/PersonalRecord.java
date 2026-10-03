package com.example.rival.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDate;

// The heaviest weight a user has lifted for one exercise
@Entity
@Table(name = "personal_records")
public class PersonalRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private AppUser user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "exercise_id")
    private Exercise exercise;

    private double weight;

    private LocalDate achievedOn;

    public PersonalRecord() {
    }

    public PersonalRecord(AppUser user, Exercise exercise, double weight, LocalDate achievedOn) {
        this.user = user;
        this.exercise = exercise;
        this.weight = weight;
        this.achievedOn = achievedOn;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public AppUser getUser() { return user; }
    public void setUser(AppUser user) { this.user = user; }

    public Exercise getExercise() { return exercise; }
    public void setExercise(Exercise exercise) { this.exercise = exercise; }

    public double getWeight() { return weight; }
    public void setWeight(double weight) { this.weight = weight; }

    public LocalDate getAchievedOn() { return achievedOn; }
    public void setAchievedOn(LocalDate achievedOn) { this.achievedOn = achievedOn; }
}
