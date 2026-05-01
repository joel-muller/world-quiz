/* (C)2026 */
package com.worldquiz.service;

import com.worldquiz.dto.AuthResponse;
import com.worldquiz.dto.LoginRequest;
import com.worldquiz.dto.RegisterRequest;
import com.worldquiz.entities.RefreshToken;
import com.worldquiz.entities.User;
import com.worldquiz.exceptions.*;
import com.worldquiz.repository.RefreshTokenRepository;
import com.worldquiz.repository.UserRepository;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public void register(RegisterRequest request) {
        log.info(request.toString());
        if (request.username().contains("@"))
            throw new ValidationException("Username cannot contain '@'");
        if (!request.email().contains("@")) throw new ValidationException("Invalid email format");

        if (userRepository.existsByUsername(request.username())
                || userRepository.existsByEmail(request.email())) {
            throw new UserAlreadyExistsException("User already exists");
        }

        String hashedPassword = passwordEncoder.encode(request.password());

        User user =
                new User(UUID.randomUUID(), request.username(), request.email(), hashedPassword);
        userRepository.save(user);
    }

    public AuthResponse login(LoginRequest request) {
        User user =
                userRepository
                        .findByUsername(request.usernameOrEmail())
                        .or(() -> userRepository.findByEmail(request.usernameOrEmail()))
                        .orElseThrow(
                                () ->
                                        new UserNotFoundException(
                                                "User with username or email "
                                                        + request.usernameOrEmail()
                                                        + " does not exist"));

        if (!passwordEncoder.matches(request.password(), user.password()))
            throw new ValidationException("Invalid credentials");

        return getNewAuthResponse(user);
    }

    public AuthResponse refresh(String refreshToken) {
        RefreshToken stored =
                refreshTokenRepository
                        .findByToken(refreshToken)
                        .orElseThrow(() -> new InvalidTokenException("Invalid refresh token"));

        if (stored.expiresAt().isBefore(Instant.now()))
            throw new TokenExpiredException("Refresh token expired");

        refreshTokenRepository.delete(stored);

        User user =
                userRepository
                        .findById(stored.userId())
                        .orElseThrow(() -> new UserNotFoundException("user not found"));

        return getNewAuthResponse(user);
    }

    private AuthResponse getNewAuthResponse(User user) {
        String newAccessToken = jwtService.generateAccessToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user);
        refreshTokenRepository.save(
                new RefreshToken(
                        UUID.randomUUID(),
                        user.id(),
                        newRefreshToken,
                        jwtService.getRefreshTokenExpirationInstant()));
        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .expiresIn(jwtService.getAccessTokenExpirationSeconds())
                .build();
    }
}
