package com.example.rival.service;

import com.example.rival.dto.PersonView;
import com.example.rival.model.AppUser;
import com.example.rival.model.Follow;
import com.example.rival.model.RequestStatus;
import com.example.rival.model.Rivalry;
import com.example.rival.model.Role;
import com.example.rival.repository.FollowRepository;
import com.example.rival.repository.RivalRequestRepository;
import com.example.rival.repository.RivalryRepository;
import com.example.rival.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;
    private final RivalryRepository rivalryRepository;
    private final RivalRequestRepository requestRepository;

    public FollowService(FollowRepository followRepository, UserRepository userRepository,
                         RivalryRepository rivalryRepository, RivalRequestRepository requestRepository) {
        this.followRepository = followRepository;
        this.userRepository = userRepository;
        this.rivalryRepository = rivalryRepository;
        this.requestRepository = requestRepository;
    }

    @Transactional
    public AppUser follow(AppUser me, Long userId) {
        AppUser target = findOtherUser(me, userId);
        if (!followRepository.existsByFollowerAndFollowed(me, target)) {
            followRepository.save(new Follow(me, target));
        }
        return target;
    }

    @Transactional
    public AppUser unfollow(AppUser me, Long userId) {
        AppUser target = findOtherUser(me, userId);
        followRepository.findByFollowerAndFollowed(me, target).ifPresent(follow -> followRepository.delete(follow));
        return target;
    }

    // The users this user follows
    public List<AppUser> following(AppUser me) {
        List<AppUser> result = new ArrayList<>();
        for (Follow follow : followRepository.findByFollower(me)) {
            result.add(follow.getFollowed());
        }
        return result;
    }

    public Set<Long> followingIds(AppUser me) {
        Set<Long> ids = new HashSet<>();
        for (AppUser user : following(me)) {
            ids.add(user.getId());
        }
        return ids;
    }

    // Everyone except me and the admin, with how each person relates to me
    public List<PersonView> people(AppUser me) {
        Set<Long> following = followingIds(me);
        Set<Long> followers = new HashSet<>();
        for (Follow follow : followRepository.findByFollowed(me)) {
            followers.add(follow.getFollower().getId());
        }
        Optional<Rivalry> myRivalry = rivalryRepository.findActiveFor(me);

        List<PersonView> people = new ArrayList<>();
        for (AppUser person : userRepository.findByRoleNotOrderByDisplayNameAsc(Role.ADMIN)) {
            if (person.getId().equals(me.getId())) {
                continue;
            }
            boolean iFollow = following.contains(person.getId());
            people.add(new PersonView(
                person, iFollow, followers.contains(person.getId()), rivalState(me, person, myRivalry, iFollow)));
        }
        return people;
    }

    private String rivalState(AppUser me, AppUser person, Optional<Rivalry> myRivalry, boolean iFollow) {
        if (myRivalry.isPresent() && myRivalry.get().opponentOf(me).getId().equals(person.getId())) {
            return PersonView.RIVALS;
        }
        if (requestRepository.existsByFromUserAndToUserAndStatus(me, person, RequestStatus.PENDING)) {
            return PersonView.SENT;
        }
        if (requestRepository.existsByFromUserAndToUserAndStatus(person, me, RequestStatus.PENDING)) {
            return PersonView.RECEIVED;
        }
        if (myRivalry.isPresent()) {
            return PersonView.I_HAVE_RIVAL;
        }
        if (rivalryRepository.findActiveFor(person).isPresent()) {
            return PersonView.THEY_HAVE_RIVAL;
        }
        return iFollow ? PersonView.CAN_REQUEST : PersonView.FOLLOW_FIRST;
    }

    private AppUser findOtherUser(AppUser me, Long userId) {
        AppUser target = userRepository.findById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (target.getId().equals(me.getId()) || target.getRole() == Role.ADMIN) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        return target;
    }
}
