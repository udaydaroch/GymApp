package com.example.rival.controller;

import com.example.rival.dto.RegisterForm;
import com.example.rival.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    // Only shows the page. Spring Security handles the POST (see SecurityConfig).
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("registerForm", new RegisterForm());
        model.addAttribute("trainers", userService.trainers());
        return "register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("registerForm") RegisterForm form,
                           BindingResult result,
                           Model model,
                           RedirectAttributes redirect) {
        if (!result.hasFieldErrors("username") && userService.usernameTaken(form.getUsername())) {
            result.rejectValue("username", "taken", "That username is taken");
        }
        if (result.hasErrors()) {
            model.addAttribute("trainers", userService.trainers());
            return "register";
        }
        userService.register(form);
        redirect.addFlashAttribute("message", "Account created. Log in to get started.");
        return "redirect:/login";
    }
}
