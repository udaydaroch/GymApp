package com.example.rival.controller;

import com.example.rival.model.AppUser;
import com.example.rival.model.WorkoutLog;
import com.example.rival.service.ProgressService;
import com.example.rival.service.RivalryService;
import com.example.rival.service.UserService;
import com.example.rival.service.WorkoutService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.List;

@Controller
public class DashboardController {

    private final UserService userService;
    private final WorkoutService workoutService;
    private final ProgressService progressService;
    private final RivalryService rivalryService;

    public DashboardController(UserService userService, WorkoutService workoutService,
                               ProgressService progressService, RivalryService rivalryService) {
        this.userService = userService;
        this.workoutService = workoutService;
        this.progressService = progressService;
        this.rivalryService = rivalryService;
    }

    @GetMapping("/")
    public String dashboard(Principal principal, Model model) {
        AppUser me = userService.current(principal);
        List<WorkoutLog> thisWeek = workoutService.thisWeek(me);

        model.addAttribute("me", me);
        model.addAttribute("workoutsThisWeek", thisWeek.size());
        model.addAttribute("volumeThisWeek", workoutService.volumeOf(thisWeek));
        model.addAttribute("badges", progressService.badgesFor(me));
        model.addAttribute("records", progressService.recordsFor(me));
        model.addAttribute("battle", rivalryService.battleView(me).orElse(null));
        model.addAttribute("incomingRequests", rivalryService.incomingRequests(me).size());
        return "dashboard";
    }
}
