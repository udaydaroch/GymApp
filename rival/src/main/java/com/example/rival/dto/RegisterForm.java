package com.example.rival.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

// What the registration page submits. Kept separate from the AppUser entity
// so a form can never set fields like the streak or the password hash directly.
public class RegisterForm {

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 20, message = "Username must be 3 to 20 characters")
    @Pattern(regexp = "^[a-zA-Z0-9_]*$", message = "Use letters, numbers and underscores only")
    private String username;

    @NotBlank(message = "Display name is required")
    @Size(max = 30, message = "Display name must be 30 characters or fewer")
    private String displayName;

    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 72, message = "Password must be 6 to 72 characters")
    private String password;

    // "MEMBER" or "TRAINER"
    private String role = "MEMBER";

    // Optional: the trainer a new member signs up under
    private Long trainerId;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public Long getTrainerId() { return trainerId; }
    public void setTrainerId(Long trainerId) { this.trainerId = trainerId; }
}
