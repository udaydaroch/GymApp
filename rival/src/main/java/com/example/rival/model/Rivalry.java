package com.example.rival.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDate;

@Entity
@Table(name = "rivalries")
public class Rivalry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_a_id")
    private AppUser userA;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_b_id")
    private AppUser userB;

    private boolean active = true;

    private LocalDate startDate;

    public Rivalry() {
    }

    public Rivalry(AppUser userA, AppUser userB, LocalDate startDate) {
        this.userA = userA;
        this.userB = userB;
        this.startDate = startDate;
    }

    public boolean isUserA(AppUser user) {
        return userA.getId().equals(user.getId());
    }

    public AppUser opponentOf(AppUser user) {
        return isUserA(user) ? userB : userA;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public AppUser getUserA() { return userA; }
    public void setUserA(AppUser userA) { this.userA = userA; }

    public AppUser getUserB() { return userB; }
    public void setUserB(AppUser userB) { this.userB = userB; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
}
