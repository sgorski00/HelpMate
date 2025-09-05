package pl.sgorski.common.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Set;
import java.util.UUID;

public record UserDto(
        @NotNull(message = "User ID cannot be empty") UUID id,
        @NotBlank(message = "Username cannot be empty") String username,
        @Email(message = "Invalid email format") @NotBlank(message = "Email cannot be empty") String email,
        @NotBlank(message = "First name cannot be empty") String firstname,
        @NotBlank(message = "Last name cannot be empty") String lastname,
        Set<String> roles
) {}