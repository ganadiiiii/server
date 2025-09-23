package ganadii.hanjum.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public class AuthDtos {
    public record SignupRequest(
            @NotBlank @Size(min = 1, max = 50) String firstName,
            @NotBlank @Size(min = 1, max = 50) String lastName,
            @Email @NotBlank String email,
            @NotBlank @Size(min = 6, max = 100) String password
    ) {}

    public record LoginRequest(
            @Email @NotBlank String email,
            @NotBlank String password
    ) {}

    public record RefreshRequest(
            @NotBlank String refreshToken
    ) {}

    public record UserResponse(UUID userId, String email, String firstName, String lastName) {}

    public record AuthResponse(String accessToken, String refreshToken, UserResponse user) {}
}
