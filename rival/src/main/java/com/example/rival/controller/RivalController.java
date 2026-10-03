package com.example.rival.controller;

import com.example.rival.exception.RivalRequestException;
import com.example.rival.model.AppUser;
import com.example.rival.service.RivalryService;
import com.example.rival.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
public class RivalController {

    private final UserService userService;
    private final RivalryService rivalryService;

    public RivalController(UserService userService, RivalryService rivalryService) {
        this.userService = userService;
        this.rivalryService = rivalryService;
    }

    @GetMapping("/rival")
    public String rival(Principal principal, Model model) {
        AppUser me = userService.current(principal);
        model.addAttribute("me", me);
        model.addAttribute("battle", rivalryService.battleView(me).orElse(null));
        model.addAttribute("incoming", rivalryService.incomingRequests(me));
        model.addAttribute("outgoing", rivalryService.outgoingRequests(me));
        return "rival";
    }

    // Sends a request to the closest free member
    @PostMapping("/rival/find")
    public String find(Principal principal, RedirectAttributes redirect) {
        try {
            AppUser match = rivalryService.requestBestMatch(userService.current(principal));
            redirect.addFlashAttribute("message", "Rival request sent to " + match.getDisplayName()
                + ", the closest match to your level. The battle starts when they accept.");
        } catch (RivalRequestException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/rival";
    }

    @PostMapping("/rival/requests/{id}/accept")
    public String accept(@PathVariable("id") Long id, Principal principal, RedirectAttributes redirect) {
        try {
            AppUser rival = rivalryService.acceptRequest(userService.current(principal), id);
            redirect.addFlashAttribute("message", "You and " + rival.getDisplayName()
                + " are now rivals. The battle runs Monday to Sunday.");
        } catch (RivalRequestException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/rival";
    }

    @PostMapping("/rival/requests/{id}/decline")
    public String decline(@PathVariable("id") Long id, Principal principal, RedirectAttributes redirect) {
        try {
            rivalryService.declineRequest(userService.current(principal), id);
            redirect.addFlashAttribute("message", "Request declined.");
        } catch (RivalRequestException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/rival";
    }

    @PostMapping("/rival/requests/{id}/cancel")
    public String cancel(@PathVariable("id") Long id, Principal principal, RedirectAttributes redirect) {
        try {
            rivalryService.cancelRequest(userService.current(principal), id);
            redirect.addFlashAttribute("message", "Request cancelled.");
        } catch (RivalRequestException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/rival";
    }

    @PostMapping("/rival/end")
    public String end(Principal principal, RedirectAttributes redirect) {
        rivalryService.endRivalry(userService.current(principal));
        redirect.addFlashAttribute("message", "Rivalry ended.");
        return "redirect:/rival";
    }
}
