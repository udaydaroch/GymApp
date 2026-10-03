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

@Entity
@Table(name = "badges")
public class Badge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private AppUser user;

    @Enumerated(EnumType.STRING)
    @Column(name = "badge_type", nullable = false)
    private BadgeType type;

    private LocalDate earnedOn;

    public Badge() {
    }

    public Badge(AppUser user, BadgeType type, LocalDate earnedOn) {
        this.user = user;
        this.type = type;
        this.earnedOn = earnedOn;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public AppUser getUser() { return user; }
    public void setUser(AppUser user) { this.user = user; }

    public BadgeType getType() { return type; }
    public void setType(BadgeType type) { this.type = type; }

    public LocalDate getEarnedOn() { return earnedOn; }
    public void setEarnedOn(LocalDate earnedOn) { this.earnedOn = earnedOn; }
}
