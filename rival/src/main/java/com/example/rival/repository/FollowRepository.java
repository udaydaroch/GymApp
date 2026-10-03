package com.example.rival.repository;

import com.example.rival.model.AppUser;
import com.example.rival.model.Follow;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    boolean existsByFollowerAndFollowed(AppUser follower, AppUser followed);

    Optional<Follow> findByFollowerAndFollowed(AppUser follower, AppUser followed);

    // Everyone this user follows
    List<Follow> findByFollower(AppUser follower);

    // Everyone who follows this user
    List<Follow> findByFollowed(AppUser followed);
}
