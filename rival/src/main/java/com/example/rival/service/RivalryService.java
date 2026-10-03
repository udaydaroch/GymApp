package com.example.rival.service;

import com.example.rival.dto.BattleView;
import com.example.rival.exception.RivalRequestException;
import com.example.rival.model.AppUser;
import com.example.rival.model.BadgeType;
import com.example.rival.model.RequestStatus;
import com.example.rival.model.RivalRequest;
import com.example.rival.model.Rivalry;
import com.example.rival.model.Role;
import com.example.rival.model.ScoringType;
import com.example.rival.model.WeeklyBattle;
import com.example.rival.model.WorkoutLog;
import com.example.rival.repository.FollowRepository;
import com.example.rival.repository.RivalRequestRepository;
import com.example.rival.repository.RivalryRepository;
import com.example.rival.repository.UserRepository;
import com.example.rival.repository.WeeklyBattleRepository;
import com.example.rival.repository.WorkoutLogRepository;
import com.example.rival.scoring.ScoringStrategy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class RivalryService {

    private final RivalryRepository rivalryRepository;
    private final WeeklyBattleRepository battleRepository;
    private final WorkoutLogRepository workoutLogRepository;
    private final UserRepository userRepository;
    private final RivalRequestRepository requestRepository;
    private final FollowRepository followRepository;
    private final ProgressService progressService;

    // One strategy per scoring type, looked up by the battle's type
    private final Map<ScoringType, ScoringStrategy> strategies = new EnumMap<>(ScoringType.class);

    // Spring passes in every class that implements ScoringStrategy
    public RivalryService(RivalryRepository rivalryRepository,
                          WeeklyBattleRepository battleRepository,
                          WorkoutLogRepository workoutLogRepository,
                          UserRepository userRepository,
                          RivalRequestRepository requestRepository,
                          FollowRepository followRepository,
                          ProgressService progressService,
                          List<ScoringStrategy> scoringStrategies) {
        this.rivalryRepository = rivalryRepository;
        this.battleRepository = battleRepository;
        this.workoutLogRepository = workoutLogRepository;
        this.userRepository = userRepository;
        this.requestRepository = requestRepository;
        this.followRepository = followRepository;
        this.progressService = progressService;
        for (ScoringStrategy strategy : scoringStrategies) {
            strategies.put(strategy.type(), strategy);
        }
    }

    public Optional<Rivalry> activeRivalry(AppUser user) {
        return rivalryRepository.findActiveFor(user);
    }

    // ------------------------------------------------------------------
    // Rival requests: a rivalry only starts once the other person accepts
    // ------------------------------------------------------------------

    public List<RivalRequest> incomingRequests(AppUser user) {
        return requestRepository.findByToUserAndStatusOrderByIdDesc(user, RequestStatus.PENDING);
    }

    public List<RivalRequest> outgoingRequests(AppUser user) {
        return requestRepository.findByFromUserAndStatusOrderByIdDesc(user, RequestStatus.PENDING);
    }

    // Sent from the People page. You can only ask someone you follow.
    @Transactional
    public AppUser sendRequest(AppUser from, Long toUserId) {
        AppUser to = userRepository.findById(toUserId).orElse(null);
        if (to == null || to.getRole() == Role.ADMIN || to.getId().equals(from.getId())) {
            throw new RivalRequestException("You can't send a rival request to that user.");
        }
        if (!followRepository.existsByFollowerAndFollowed(from, to)) {
            throw new RivalRequestException("Follow " + to.getDisplayName() + " before sending a rival request.");
        }
        createRequest(from, to);
        return to;
    }

    // "Find me a rival": sends a request to the free member whose recent
    // training volume is closest to the user's.
    @Transactional
    public AppUser requestBestMatch(AppUser user) {
        if (activeRivalry(user).isPresent()) {
            throw new RivalRequestException("You already have a rival. End that rivalry first.");
        }
        long myVolume = recentVolume(user);
        AppUser best = null;
        long bestGap = Long.MAX_VALUE;

        for (AppUser candidate : userRepository.findByRoleOrderByDisplayNameAsc(Role.MEMBER)) {
            if (candidate.getId().equals(user.getId())
                || activeRivalry(candidate).isPresent()
                || hasPendingRequestBetween(user, candidate)) {
                continue;
            }
            long gap = Math.abs(recentVolume(candidate) - myVolume);
            if (gap < bestGap) {
                bestGap = gap;
                best = candidate;
            }
        }
        if (best == null) {
            throw new RivalRequestException("Nobody new is free right now. Check your pending requests, or try later.");
        }
        createRequest(user, best);
        return best;
    }

    // Accepting creates the rivalry and opens this week's battle
    @Transactional
    public AppUser acceptRequest(AppUser user, Long requestId) {
        RivalRequest request = pendingRequest(requestId);
        if (!request.getToUser().getId().equals(user.getId())) {
            throw new RivalRequestException("That request isn't yours to accept.");
        }
        AppUser from = request.getFromUser();
        if (activeRivalry(user).isPresent()) {
            throw new RivalRequestException("You already have a rival. End that rivalry first.");
        }
        if (activeRivalry(from).isPresent()) {
            throw new RivalRequestException(from.getDisplayName() + " already has a rival now.");
        }

        request.setStatus(RequestStatus.ACCEPTED);
        requestRepository.save(request);

        Rivalry rivalry = rivalryRepository.save(new Rivalry(from, user, LocalDate.now()));
        battleRepository.save(new WeeklyBattle(
            rivalry, WorkoutService.weekStart(LocalDate.now()), ScoringType.IMPROVEMENT));

        // Both people are taken now, so their other open requests are closed
        closeOpenRequests(user);
        closeOpenRequests(from);
        return from;
    }

    @Transactional
    public void declineRequest(AppUser user, Long requestId) {
        RivalRequest request = pendingRequest(requestId);
        if (!request.getToUser().getId().equals(user.getId())) {
            throw new RivalRequestException("That request isn't yours to decline.");
        }
        request.setStatus(RequestStatus.DECLINED);
        requestRepository.save(request);
    }

    @Transactional
    public void cancelRequest(AppUser user, Long requestId) {
        RivalRequest request = pendingRequest(requestId);
        if (!request.getFromUser().getId().equals(user.getId())) {
            throw new RivalRequestException("That request isn't yours to cancel.");
        }
        request.setStatus(RequestStatus.CANCELLED);
        requestRepository.save(request);
    }

    private void createRequest(AppUser from, AppUser to) {
        if (activeRivalry(from).isPresent()) {
            throw new RivalRequestException("You already have a rival. End that rivalry first.");
        }
        if (activeRivalry(to).isPresent()) {
            throw new RivalRequestException(to.getDisplayName() + " already has a rival.");
        }
        if (requestRepository.existsByFromUserAndToUserAndStatus(from, to, RequestStatus.PENDING)) {
            throw new RivalRequestException("You've already sent " + to.getDisplayName() + " a request.");
        }
        if (requestRepository.existsByFromUserAndToUserAndStatus(to, from, RequestStatus.PENDING)) {
            throw new RivalRequestException(
                to.getDisplayName() + " has already sent you a request. Accept it on the Rival page.");
        }
        requestRepository.save(new RivalRequest(from, to, LocalDate.now()));
    }

    private RivalRequest pendingRequest(Long requestId) {
        return requestRepository.findById(requestId)
            .filter(request -> request.getStatus() == RequestStatus.PENDING)
            .orElseThrow(() -> new RivalRequestException("That request is no longer open."));
    }

    private boolean hasPendingRequestBetween(AppUser a, AppUser b) {
        return requestRepository.existsByFromUserAndToUserAndStatus(a, b, RequestStatus.PENDING)
            || requestRepository.existsByFromUserAndToUserAndStatus(b, a, RequestStatus.PENDING);
    }

    private void closeOpenRequests(AppUser user) {
        for (RivalRequest request : incomingRequests(user)) {
            request.setStatus(RequestStatus.DECLINED);
            requestRepository.save(request);
        }
        for (RivalRequest request : outgoingRequests(user)) {
            request.setStatus(RequestStatus.CANCELLED);
            requestRepository.save(request);
        }
    }

    // ------------------------------------------------------------------
    // Rivalries and weekly battles
    // ------------------------------------------------------------------

    // A user's score for one battle week, using the given scoring strategy
    public long score(AppUser user, LocalDate weekStart, ScoringType type) {
        List<WorkoutLog> thisWeek =
            workoutLogRepository.findByUserAndWorkoutDateBetween(user, weekStart, weekStart.plusDays(6));
        List<WorkoutLog> previousFour =
            workoutLogRepository.findByUserAndWorkoutDateBetween(user, weekStart.minusDays(28), weekStart.minusDays(1));
        return strategies.get(type).score(thisWeek, previousFour);
    }

    @Transactional
    public void endRivalry(AppUser user) {
        activeRivalry(user).ifPresent(rivalry -> {
            rivalry.setActive(false);
            rivalryRepository.save(rivalry);
        });
    }

    // Everything the rival page shows, or empty if the user has no rival
    @Transactional
    public Optional<BattleView> battleView(AppUser user) {
        Optional<Rivalry> found = activeRivalry(user);
        if (found.isEmpty()) {
            return Optional.empty();
        }
        rollOver(); // make sure old weeks are closed and this week's battle exists

        Rivalry rivalry = found.get();
        AppUser opponent = rivalry.opponentOf(user);
        LocalDate monday = WorkoutService.weekStart(LocalDate.now());
        WeeklyBattle battle = currentBattle(rivalry, monday);

        List<WeeklyBattle> past = battleRepository.findByRivalryAndClosedTrueOrderByWeekStartDesc(rivalry);
        int myWins = 0;
        int theirWins = 0;
        for (WeeklyBattle old : past) {
            if (old.getWinner() == null) {
                continue;
            }
            if (old.getWinner().getId().equals(user.getId())) {
                myWins++;
            } else {
                theirWins++;
            }
        }

        return Optional.of(new BattleView(
            opponent,
            score(user, monday, battle.getScoringType()),
            score(opponent, monday, battle.getScoringType()),
            battle.getScoringType(),
            monday,
            monday.plusDays(6),
            past,
            myWins,
            theirWins));
    }

    // Runs every Monday at midnight, and also whenever someone opens the rival page
    // (so it still works if the app was switched off on Monday).
    @Scheduled(cron = "0 0 0 * * MON")
    @Transactional
    public void rollOver() {
        LocalDate monday = WorkoutService.weekStart(LocalDate.now());

        // 1. Close battles from earlier weeks and record the winner
        for (WeeklyBattle battle : battleRepository.findByClosedFalse()) {
            if (battle.getWeekStart().isBefore(monday)) {
                close(battle);
            }
        }
        // 2. Open this week's battle for every active rivalry
        for (Rivalry rivalry : rivalryRepository.findByActiveTrue()) {
            currentBattle(rivalry, monday);
        }
    }

    // A trainer picks the battle type for this week's battles involving their clients
    @Transactional
    public int setScoringForTrainer(AppUser trainer, ScoringType type) {
        rollOver();
        int changed = 0;
        for (WeeklyBattle battle : battleRepository.findByClosedFalse()) {
            Rivalry rivalry = battle.getRivalry();
            if (isClientOf(rivalry.getUserA(), trainer) || isClientOf(rivalry.getUserB(), trainer)) {
                battle.setScoringType(type);
                battleRepository.save(battle);
                changed++;
            }
        }
        return changed;
    }

    private WeeklyBattle currentBattle(Rivalry rivalry, LocalDate monday) {
        return battleRepository.findByRivalryAndWeekStart(rivalry, monday)
            .orElseGet(() -> battleRepository.save(new WeeklyBattle(rivalry, monday, ScoringType.IMPROVEMENT)));
    }

    private void close(WeeklyBattle battle) {
        Rivalry rivalry = battle.getRivalry();
        long scoreA = score(rivalry.getUserA(), battle.getWeekStart(), battle.getScoringType());
        long scoreB = score(rivalry.getUserB(), battle.getWeekStart(), battle.getScoringType());

        battle.setScoreA(scoreA);
        battle.setScoreB(scoreB);
        battle.setClosed(true);
        if (scoreA > scoreB) {
            battle.setWinner(rivalry.getUserA());
        } else if (scoreB > scoreA) {
            battle.setWinner(rivalry.getUserB());
        }
        battleRepository.save(battle);

        if (battle.getWinner() != null) {
            progressService.award(battle.getWinner(), BadgeType.BATTLE_WON);
        }
    }

    private boolean isClientOf(AppUser user, AppUser trainer) {
        return user.getTrainer() != null && user.getTrainer().getId().equals(trainer.getId());
    }

    // Weight moved in the last 4 weeks
    private long recentVolume(AppUser user) {
        LocalDate today = LocalDate.now();
        long total = 0;
        for (WorkoutLog log : workoutLogRepository.findByUserAndWorkoutDateBetween(user, today.minusDays(28), today)) {
            total += log.getTotalVolume();
        }
        return total;
    }
}
