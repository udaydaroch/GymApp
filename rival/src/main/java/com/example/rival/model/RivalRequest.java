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

// An invitation to become rivals. A Rivalry is only created once it is accepted.
@Entity
@Table(name = "rival_requests")
public class RivalRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "from_user_id")
    private AppUser fromUser;

    @ManyToOne(optional = false)
    @JoinColumn(name = "to_user_id")
    private AppUser toUser;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestStatus status = RequestStatus.PENDING;

    private LocalDate createdOn;

    public RivalRequest() {
    }

    public RivalRequest(AppUser fromUser, AppUser toUser, LocalDate createdOn) {
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.createdOn = createdOn;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public AppUser getFromUser() { return fromUser; }
    public void setFromUser(AppUser fromUser) { this.fromUser = fromUser; }

    public AppUser getToUser() { return toUser; }
    public void setToUser(AppUser toUser) { this.toUser = toUser; }

    public RequestStatus getStatus() { return status; }
    public void setStatus(RequestStatus status) { this.status = status; }

    public LocalDate getCreatedOn() { return createdOn; }
    public void setCreatedOn(LocalDate createdOn) { this.createdOn = createdOn; }
}
