package com.example.rival.service;

import com.example.rival.dto.RegisterForm;
import com.example.rival.model.AppUser;
import com.example.rival.model.Role;
import com.example.rival.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // The logged-in user, loaded from the database
    public AppUser current(Principal principal) {
        return userRepository.findByUsername(principal.getName())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
    }

    public boolean usernameTaken(String username) {
        return userRepository.existsByUsername(username);
    }

    public List<AppUser> trainers() {
        return userRepository.findByRoleOrderByDisplayNameAsc(Role.TRAINER);
    }

    @Transactional
    public AppUser register(RegisterForm form) {
        // Only MEMBER or TRAINER can be chosen on the form; never ADMIN
        Role role = "TRAINER".equals(form.getRole()) ? Role.TRAINER : Role.MEMBER;

        AppUser user = new AppUser(
            form.getUsername(),
            passwordEncoder.encode(form.getPassword()),
            form.getDisplayName().trim(),
            role);

        if (role == Role.MEMBER && form.getTrainerId() != null) {
            userRepository.findById(form.getTrainerId())
                .filter(trainer -> trainer.getRole() == Role.TRAINER)
                .ifPresent(user::setTrainer);
        }
        return userRepository.save(user);
    }
}
