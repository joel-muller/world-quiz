/* (C)2026 */
package com.worldquiz.service;

import com.worldquiz.dto.AuthResponse;
import com.worldquiz.dto.LoginRequest;
import com.worldquiz.dto.RegisterRequest;
import com.worldquiz.dto.VerifyEmailRequest;
import com.worldquiz.entities.EmailSendLog;
import com.worldquiz.entities.EmailVerificationToken;
import com.worldquiz.entities.RefreshToken;
import com.worldquiz.entities.User;
import com.worldquiz.exceptions.*;
import com.worldquiz.repository.EmailSendLogRepository;
import com.worldquiz.repository.EmailVerificationTokenRepository;
import com.worldquiz.repository.RefreshTokenRepository;
import com.worldquiz.repository.UserRepository;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
    private final MailService mailService;
    private final EmailVerificationTokenRepository emailVerificationTokenRepository;
    private final EmailSendLogRepository emailSendLogRepository;

    @Value("${mailgun.threshold.max}")
    private int maxNumberOfMails;

    @Value("${mailgun.threshold.user}")
    private int maxNumberOfMailsPerUser;

    public void register(RegisterRequest request, String frontendBaseUrl) {
        if (userRepository.existsByUsername(request.username())
                || userRepository.existsByEmail(request.email())) {
            throw new UserAlreadyExistsException("User already exists");
        }

        String hashedPassword = passwordEncoder.encode(request.password());

        User user =
                new User(
                        UUID.randomUUID(),
                        request.username(),
                        request.email(),
                        hashedPassword,
                        false);
        userRepository.save(user);

        sendVerification(user, frontendBaseUrl);
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

    public void verifyEmail(VerifyEmailRequest request) {
        EmailVerificationToken token =
                emailVerificationTokenRepository
                        .findByToken(request.token())
                        .orElseThrow(() -> new InvalidTokenException("Token does not exist"));
        if (token.expiresAt().isBefore(Instant.now()))
            throw new TokenExpiredException("Verify Mail token expired");
        User user =
                userRepository
                        .findById(token.userId())
                        .orElseThrow(() -> new UserNotFoundException("User not found"));
        if (user.emailConfirmed()) throw new EmailAlreadyConfirmed("Email Already confirmed");

        emailVerificationTokenRepository.deleteByToken(token.token());
        userRepository.save(user.confirmEmail());
    }

    public void resendVerification(String email, String frontendBaseUrl) {
        User user =
                userRepository
                        .findByEmail(email)
                        .orElseThrow(
                                () ->
                                        new UserNotFoundException(
                                                "User with the mail does not exist" + email));
        this.sendVerification(user, frontendBaseUrl);
    }

    private void sendVerification(User user, String frontendBaseUrl) {
        ZoneId zone = ZoneId.systemDefault();
        LocalDate today = LocalDate.now(zone);

        Instant start = today.atStartOfDay(zone).toInstant();
        Instant end = today.plusDays(1).atStartOfDay(zone).toInstant();

        long totalToday = emailSendLogRepository.countBySentAtBetween(start, end);

        long userToday =
                emailSendLogRepository.countByUserIdAndSentAtBetween(user.id(), start, end);

        if (totalToday >= this.maxNumberOfMails || userToday >= maxNumberOfMailsPerUser) {
            throw new TooManyMailsSent("Too Many Mails sent for today, try again Tomorrow");
        }

        Optional<EmailSendLog> lastMail =
                emailSendLogRepository.findTopByUserIdOrderBySentAtDesc(user.id());
        if (lastMail.isPresent()
                && lastMail.get().sentAt().isAfter(Instant.now().minusSeconds(60))) {
            throw new TooManyMailsSent("Please wait before requesting another email");
        }

        if (user.emailConfirmed()) throw new EmailAlreadyConfirmed("Email Already confirmed");

        String token = UUID.randomUUID().toString();
        Instant expiration = Instant.now().plus(Duration.ofHours(24));
        EmailVerificationToken emailVerificationToken =
                new EmailVerificationToken(
                        UUID.randomUUID(), user.id(), token, expiration, Instant.now());

        this.emailVerificationTokenRepository.deleteAllByUserId(user.id());
        this.emailVerificationTokenRepository.save(emailVerificationToken);
        this.emailSendLogRepository.save(
                new EmailSendLog(UUID.randomUUID(), user.id(), Instant.now()));
        this.mailService.sendEmailVerificationMail(
                user.username(), user.email(), token, frontendBaseUrl);
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
                .build();
    }
}
