package com.example.rival.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

// "follower follows followed". One row per direction.
@Entity
@Table(name = "follows")
public class Follow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "follower_id")
    private AppUser follower;

    @ManyToOne(optional = false)
    @JoinColumn(name = "followed_id")
    private AppUser followed;

    public Follow() {
    }

    public Follow(AppUser follower, AppUser followed) {
        this.follower = follower;
        this.followed = followed;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public AppUser getFollower() { return follower; }
    public void setFollower(AppUser follower) { this.follower = follower; }

    public AppUser getFollowed() { return followed; }
    public void setFollowed(AppUser followed) { this.followed = followed; }
}
