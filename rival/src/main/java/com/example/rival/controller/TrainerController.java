package com.example.rival.controller;

import com.example.rival.model.AppUser;
import com.example.rival.model.ScoringType;
import com.example.rival.service.RivalryService;
import com.example.rival.service.TrainerService;
import com.example.rival.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

// Everything under /trainer needs the TRAINER role (see SecurityConfig)
@Controller
public class TrainerController {

    private final UserService userService;
    private final TrainerService trainerService;
    private final RivalryService rivalryService;

    public TrainerController(UserService userService, TrainerService trainerService,
                             RivalryService rivalryService) {
        this.userService = userService;
        this.trainerService = trainerService;
        this.rivalryService = rivalryService;
    }

    @GetMapping("/trainer")
    public String clients(Principal principal, Model model) {
        AppUser trainer = userService.current(principal);
        model.addAttribute("clients", trainerService.clientSummaries(trainer));
        model.addAttribute("scoringTypes", ScoringType.values());
        return "trainer";
    }

    @PostMapping("/trainer/scoring")
    public String setScoring(@RequestParam("type") ScoringType type,
                             Principal principal,
                             RedirectAttributes redirect) {
        int changed = rivalryService.setScoringForTrainer(userService.current(principal), type);
        redirect.addFlashAttribute("message",
            "This week's battle type is now " + type.getLabel() + " for " + changed + " battle(s).");
        return "redirect:/trainer";
    }
}
