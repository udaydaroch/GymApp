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

// Named AppUser (table "users") because USER is a reserved word in SQL.
@Entity
@Table(name = "users")
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    // BCrypt hash, never the plain password
    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String displayName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    // A member's trainer (empty for trainers and for members without one)
    @ManyToOne
    @JoinColumn(name = "trainer_id")
    private AppUser trainer;

    private int currentStreak;
    private int longestStreak;
    private LocalDate lastWorkoutDate;

    public AppUser() {
    }

    public AppUser(String username, String password, String displayName, Role role) {
        this.username = username;
        this.password = password;
        this.displayName = displayName;
        this.role = role;
    }

    // The stored streak only counts if the last workout was today or yesterday.
    public int getActiveStreak() {
        if (lastWorkoutDate == null) {
            return 0;
        }
        return lastWorkoutDate.isBefore(LocalDate.now().minusDays(1)) ? 0 : currentStreak;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public AppUser getTrainer() { return trainer; }
    public void setTrainer(AppUser trainer) { this.trainer = trainer; }

    public int getCurrentStreak() { return currentStreak; }
    public void setCurrentStreak(int currentStreak) { this.currentStreak = currentStreak; }

    public int getLongestStreak() { return longestStreak; }
    public void setLongestStreak(int longestStreak) { this.longestStreak = longestStreak; }

    public LocalDate getLastWorkoutDate() { return lastWorkoutDate; }
    public void setLastWorkoutDate(LocalDate lastWorkoutDate) { this.lastWorkoutDate = lastWorkoutDate; }
}
