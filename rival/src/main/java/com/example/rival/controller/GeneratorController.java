package com.example.rival.controller;

import com.example.rival.dto.GeneratorForm;
import com.example.rival.dto.WorkoutForm;
import com.example.rival.service.GeneratorService;
import com.example.rival.service.UserService;
import com.example.rival.service.WorkoutService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;

@Controller
public class GeneratorController {

    private final UserService userService;
    private final GeneratorService generatorService;
    private final WorkoutService workoutService;

    public GeneratorController(UserService userService, GeneratorService generatorService,
                               WorkoutService workoutService) {
        this.userService = userService;
        this.generatorService = generatorService;
        this.workoutService = workoutService;
    }

    @GetMapping("/no-excuses")
    public String questions(Model model) {
        model.addAttribute("generatorForm", new GeneratorForm());
        return "generator";
    }

    // Builds a workout and shows it in the normal workout form, ready to log
    @PostMapping("/no-excuses")
    public String generate(@Valid @ModelAttribute("generatorForm") GeneratorForm form,
                           BindingResult result,
                           Principal principal,
                           Model model) {
        if (result.hasErrors()) {
            return "generator";
        }
        WorkoutForm workout = generatorService.generate(userService.current(principal), form);
        if (workout.getRows().isEmpty()) {
            result.reject("none", "Nothing fits those answers. Try a different sore area.");
            return "generator";
        }
        model.addAttribute("workoutForm", workout);
        model.addAttribute("exercises", workoutService.allExercises());
        model.addAttribute("notice", "Here's your " + form.getMinutes()
            + "-minute plan. Do it, tweak the numbers to what you actually did, then log it.");
        return "workout-form";
    }
}
