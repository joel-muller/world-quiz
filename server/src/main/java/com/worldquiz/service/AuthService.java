/* (C)2026 */
package com.worldquiz.service;

import com.worldquiz.dto.*;
import com.worldquiz.entities.*;
import com.worldquiz.exceptions.*;
import com.worldquiz.repository.*;
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
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    @Value("${mailgun.threshold.max}")
    private int maxNumberOfMails;

    @Value("${mailgun.threshold.user}")
    private int maxNumberOfMailsPerUser;

    public void register(RegisterRequest request, String frontendBaseUrl) {
        log.info("Register attempt username={} email={}", request.username(), request.email());

        if (userRepository.existsByUsername(request.username())
                || userRepository.existsByEmail(request.email())) {
            log.warn("Register failed - user already exists username={}", request.username());
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
        log.info("User registered successfully userId={}", user.id());
        sendVerification(user, frontendBaseUrl);
    }

    public TokenDto login(LoginRequest request) {
        log.info("Login attempt identifier={}", request.usernameOrEmail());
        User user =
                userRepository
                        .findByUsername(request.usernameOrEmail())
                        .or(() -> userRepository.findByEmail(request.usernameOrEmail()))
                        .orElseThrow(
                                () -> {
                                    log.warn(
                                            "Login failed - user not found identifier={}",
                                            request.usernameOrEmail());
                                    return new UserNotFoundException(
                                            "User with username or email "
                                                    + request.usernameOrEmail()
                                                    + " does not exist");
                                });

        if (!passwordEncoder.matches(request.password(), user.password())) {
            log.warn("Login failed - invalid credentials userId={}", user.id());
            throw new ValidationException("Invalid credentials");
        }

        log.info("Login successful userId={}", user.id());

        return getNewAuthResponse(user);
    }

    public TokenDto refresh(String refreshToken) {
        log.info("Refresh token attempt");

        RefreshToken stored =
                refreshTokenRepository
                        .findByToken(refreshToken)
                        .orElseThrow(
                                () -> {
                                    log.warn("Refresh failed - invalid token");
                                    return new InvalidTokenException("Invalid refresh token");
                                });

        if (stored.expiresAt().isBefore(Instant.now())) {
            log.warn("Refresh failed - expired token userId={}", stored.userId());
            throw new TokenExpiredException("Refresh token expired");
        }

        refreshTokenRepository.delete(stored);

        User user =
                userRepository
                        .findById(stored.userId())
                        .orElseThrow(() -> new UserNotFoundException("user not found"));

        log.info("Refresh successful userId={}", user.id());

        return getNewAuthResponse(user);
    }

    public void verifyEmail(VerifyEmailRequest request) {
        log.info("Email verification attempt");

        EmailVerificationToken token =
                emailVerificationTokenRepository
                        .findByToken(request.token())
                        .orElseThrow(
                                () -> {
                                    log.warn("Email verification failed - invalid token");
                                    return new InvalidTokenException("Token does not exist");
                                });

        if (token.expiresAt().isBefore(Instant.now())) {
            log.warn("Email verification failed - expired token userId={}", token.userId());
            throw new TokenExpiredException("Verify Mail token expired");
        }

        User user =
                userRepository
                        .findById(token.userId())
                        .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (user.emailConfirmed()) {
            log.warn("Email verification skipped - already confirmed userId={}", user.id());
            throw new EmailAlreadyConfirmedException("Email Already confirmed");
        }

        emailVerificationTokenRepository.deleteByToken(token.token());
        userRepository.save(user.confirmEmail());

        log.info("Email verified successfully userId={}", user.id());
    }

    public void resendVerification(String email, String frontendBaseUrl) {
        log.info("Resend verification request email={}", email);

        User user =
                userRepository
                        .findByEmail(email)
                        .orElseThrow(
                                () -> {
                                    log.warn(
                                            "Resend verification failed - user not found email={}",
                                            email);
                                    return new UserNotFoundException(
                                            "User with the mail does not exist" + email);
                                });

        sendVerification(user, frontendBaseUrl);
    }

    public void forgotPassword(String email, String frontendBaseUrl) {
        log.info("Forgot password request email={}", email);

        User user =
                userRepository
                        .findByEmail(email)
                        .orElseThrow(() -> new UserNotFoundException("User not found"));

        String token = UUID.randomUUID().toString();

        Instant expiration = Instant.now().plus(Duration.ofHours(1));

        PasswordResetToken resetToken =
                new PasswordResetToken(
                        UUID.randomUUID(), user.id(), token, expiration, Instant.now());

        passwordResetTokenRepository.deleteAllByUserId(user.id());

        passwordResetTokenRepository.save(resetToken);

        mailService.sendPasswordResetMail(user.username(), user.email(), token, frontendBaseUrl);

        log.info("Password reset email sent userId={}", user.id());
    }

    public void resetPassword(ResetPasswordRequest request) {
        log.info("Reset password attempt");

        PasswordResetToken token =
                passwordResetTokenRepository
                        .findByToken(request.token())
                        .orElseThrow(() -> new InvalidTokenException("Invalid token"));

        if (token.expiresAt().isBefore(Instant.now())) {
            throw new TokenExpiredException("Reset token expired");
        }

        User user =
                userRepository
                        .findById(token.userId())
                        .orElseThrow(() -> new UserNotFoundException("User not found"));

        String hashedPassword = passwordEncoder.encode(request.newPassword());

        User updatedUser = user.withNewPassword(hashedPassword);

        userRepository.save(updatedUser);
        passwordResetTokenRepository.deleteByToken(token.token());
        refreshTokenRepository.deleteByUserId(user.id());

        log.info("Password reset successful userId={}", user.id());
    }

    private void sendVerification(User user, String frontendBaseUrl) {
        log.info("Sending verification email userId={}", user.id());

        ZoneId zone = ZoneId.systemDefault();
        LocalDate today = LocalDate.now(zone);

        Instant start = today.atStartOfDay(zone).toInstant();
        Instant end = today.plusDays(1).atStartOfDay(zone).toInstant();

        long totalToday = emailSendLogRepository.countBySentAtBetween(start, end);
        long userToday =
                emailSendLogRepository.countByUserIdAndSentAtBetween(user.id(), start, end);

        if (totalToday >= this.maxNumberOfMails || userToday >= maxNumberOfMailsPerUser) {
            log.warn("Email limit reached userId={}", user.id());
            throw new TooManyMailsSentException(
                    "Too Many Mails sent for today, try again Tomorrow");
        }

        Optional<EmailSendLog> lastMail =
                emailSendLogRepository.findTopByUserIdOrderBySentAtDesc(user.id());

        if (lastMail.isPresent()
                && lastMail.get().sentAt().isAfter(Instant.now().minusSeconds(60))) {
            log.warn("Email rate limit hit userId={}", user.id());
            throw new TooManyMailsSentException("Please wait before requesting another email");
        }

        if (user.emailConfirmed()) {
            log.warn("Email already confirmed userId={}", user.id());
            throw new EmailAlreadyConfirmedException("Email Already confirmed");
        }

        String token = UUID.randomUUID().toString();
        Instant expiration = Instant.now().plus(Duration.ofHours(24));

        EmailVerificationToken emailVerificationToken =
                new EmailVerificationToken(
                        UUID.randomUUID(), user.id(), token, expiration, Instant.now());

        emailVerificationTokenRepository.deleteAllByUserId(user.id());
        emailVerificationTokenRepository.save(emailVerificationToken);

        emailSendLogRepository.save(new EmailSendLog(UUID.randomUUID(), user.id(), Instant.now()));

        mailService.sendEmailVerificationMail(
                user.username(), user.email(), token, frontendBaseUrl);

        log.info("Verification email sent userId={}", user.id());
    }

    private TokenDto getNewAuthResponse(User user) {
        String newAccessToken = jwtService.generateAccessToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user);

        refreshTokenRepository.save(
                new RefreshToken(
                        UUID.randomUUID(),
                        user.id(),
                        newRefreshToken,
                        jwtService.getRefreshTokenExpirationInstant()));

        return TokenDto.builder().accessToken(newAccessToken).refreshToken(newRefreshToken).build();
    }
}
