/* (C)2026 */
package com.worldquiz.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.worldquiz.dto.LoginRequest;
import com.worldquiz.dto.RegisterRequest;
import com.worldquiz.dto.TokenDto;
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
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private RefreshTokenRepository refreshTokenRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtService jwtService;
    @Mock private MailService mailService;
    @Mock private EmailVerificationTokenRepository emailVerificationTokenRepository;
    @Mock private EmailSendLogRepository emailSendLogRepository;

    @InjectMocks private AuthService authService;

    private static final String FRONTEND_URL = "http://localhost:3000";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(authService, "maxNumberOfMails", 100);
        ReflectionTestUtils.setField(authService, "maxNumberOfMailsPerUser", 5);
    }

    @Nested
    class RegisterTests {

        @Test
        void register_Success() {
            RegisterRequest request =
                    new RegisterRequest("testuser", "test@mail.com", "password123");
            when(userRepository.existsByUsername(request.username())).thenReturn(false);
            when(userRepository.existsByEmail(request.email())).thenReturn(false);
            when(passwordEncoder.encode(request.password())).thenReturn("hashed_password");

            when(emailSendLogRepository.countBySentAtBetween(any(), any())).thenReturn(0L);
            when(emailSendLogRepository.countByUserIdAndSentAtBetween(any(), any(), any()))
                    .thenReturn(0L);
            when(emailSendLogRepository.findTopByUserIdOrderBySentAtDesc(any()))
                    .thenReturn(Optional.empty());

            authService.register(request, FRONTEND_URL);

            ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
            verify(userRepository).save(userCaptor.capture());

            User savedUser = userCaptor.getValue();
            assertThat(savedUser.username()).isEqualTo("testuser");
            assertThat(savedUser.email()).isEqualTo("test@mail.com");
            assertThat(savedUser.password()).isEqualTo("hashed_password");
            assertThat(savedUser.emailConfirmed()).isFalse();

            verify(emailVerificationTokenRepository).save(any(EmailVerificationToken.class));
            verify(mailService)
                    .sendEmailVerificationMail(
                            eq("testuser"), eq("test@mail.com"), anyString(), eq(FRONTEND_URL));
        }

        @Test
        void register_ThrowsException_WhenUserAlreadyExists() {
            RegisterRequest request = new RegisterRequest("existing", "test@mail.com", "password");
            when(userRepository.existsByUsername("existing")).thenReturn(true);

            assertThatThrownBy(() -> authService.register(request, FRONTEND_URL))
                    .isInstanceOf(UserAlreadyExistsException.class)
                    .hasMessageContaining("User already exists");

            verify(userRepository, never()).save(any());
        }

        @Test
        void register_ThrowsException_WhenTooManyMailsSentGlobally() {
            RegisterRequest request = new RegisterRequest("testuser", "test@mail.com", "password");
            when(userRepository.existsByUsername(any())).thenReturn(false);
            when(passwordEncoder.encode(any())).thenReturn("hash");

            when(emailSendLogRepository.countBySentAtBetween(any(), any())).thenReturn(100L);

            assertThatThrownBy(() -> authService.register(request, FRONTEND_URL))
                    .isInstanceOf(TooManyMailsSentException.class)
                    .hasMessageContaining("Too Many Mails sent for today");
        }

        @Test
        void register_ThrowsException_WhenSpammingWithinOneMinute() {
            RegisterRequest request = new RegisterRequest("testuser", "test@mail.com", "password");
            UUID userId = UUID.randomUUID();

            when(userRepository.existsByUsername(any())).thenReturn(false);
            when(passwordEncoder.encode(any())).thenReturn("hash");

            EmailSendLog recentLog =
                    new EmailSendLog(UUID.randomUUID(), userId, Instant.now().minusSeconds(10));
            when(emailSendLogRepository.findTopByUserIdOrderBySentAtDesc(any()))
                    .thenReturn(Optional.of(recentLog));

            assertThatThrownBy(() -> authService.register(request, FRONTEND_URL))
                    .isInstanceOf(TooManyMailsSentException.class)
                    .hasMessageContaining("Please wait before requesting another email");
        }
    }

    @Nested
    class LoginTests {

        @Test
        void login_Success_WithUsername() {
            LoginRequest request = new LoginRequest("testuser", "password123");
            User user =
                    new User(
                            UUID.randomUUID(),
                            "testuser",
                            "test@mail.com",
                            "hashed_password",
                            true);

            when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
            when(passwordEncoder.matches("password123", "hashed_password")).thenReturn(true);
            when(jwtService.generateAccessToken(user)).thenReturn("access_token");
            when(jwtService.generateRefreshToken(user)).thenReturn("refresh_token");
            when(jwtService.getRefreshTokenExpirationInstant())
                    .thenReturn(Instant.now().plusSeconds(3600));

            TokenDto response = authService.login(request);

            assertThat(response).isNotNull();
            assertThat(response.accessToken()).isEqualTo("access_token");
            assertThat(response.refreshToken()).isEqualTo("refresh_token");
            verify(refreshTokenRepository).save(any(RefreshToken.class));
        }

        @Test
        void login_ThrowsException_WhenUserNotFound() {
            LoginRequest request = new LoginRequest("unknown", "password");
            when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());
            when(userRepository.findByEmail("unknown")).thenReturn(Optional.empty());

            assertThatThrownBy(() -> authService.login(request))
                    .isInstanceOf(UserNotFoundException.class);
        }

        @Test
        void login_ThrowsException_WhenPasswordInvalid() {
            LoginRequest request = new LoginRequest("testuser", "wrong_password");
            User user =
                    new User(
                            UUID.randomUUID(),
                            "testuser",
                            "test@mail.com",
                            "hashed_password",
                            true);

            when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
            when(passwordEncoder.matches("wrong_password", "hashed_password")).thenReturn(false);

            assertThatThrownBy(() -> authService.login(request))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("Invalid credentials");
        }
    }

    @Nested
    class RefreshTokenTests {

        @Test
        void refresh_Success() {
            String tokenString = "valid_refresh_token";
            UUID userId = UUID.randomUUID();
            RefreshToken storedToken =
                    new RefreshToken(
                            UUID.randomUUID(), userId, tokenString, Instant.now().plusSeconds(60));
            User user = new User(userId, "testuser", "test@mail.com", "hash", true);

            when(refreshTokenRepository.findByToken(tokenString))
                    .thenReturn(Optional.of(storedToken));
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(jwtService.generateAccessToken(user)).thenReturn("new_access");
            when(jwtService.generateRefreshToken(user)).thenReturn("new_refresh");

            TokenDto result = authService.refresh(tokenString);

            assertThat(result.accessToken()).isEqualTo("new_access");
            verify(refreshTokenRepository).delete(storedToken);
            verify(refreshTokenRepository).save(any(RefreshToken.class));
        }

        @Test
        void refresh_ThrowsException_WhenTokenExpired() {
            String tokenString = "expired_refresh_token";
            RefreshToken expiredToken =
                    new RefreshToken(
                            UUID.randomUUID(),
                            UUID.randomUUID(),
                            tokenString,
                            Instant.now().minusSeconds(10));

            when(refreshTokenRepository.findByToken(tokenString))
                    .thenReturn(Optional.of(expiredToken));

            assertThatThrownBy(() -> authService.refresh(tokenString))
                    .isInstanceOf(TokenExpiredException.class);

            verify(refreshTokenRepository, never()).delete(any());
        }
    }

    @Nested
    class VerifyEmailTests {

        @Test
        void verifyEmail_Success() {
            VerifyEmailRequest request = new VerifyEmailRequest("valid_token");
            UUID userId = UUID.randomUUID();
            EmailVerificationToken token =
                    new EmailVerificationToken(
                            UUID.randomUUID(),
                            userId,
                            "valid_token",
                            Instant.now().plusSeconds(600),
                            Instant.now());
            User user = mock(User.class);
            User confirmedUser = mock(User.class);

            when(emailVerificationTokenRepository.findByToken("valid_token"))
                    .thenReturn(Optional.of(token));
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(user.emailConfirmed()).thenReturn(false);
            when(user.confirmEmail()).thenReturn(confirmedUser);

            authService.verifyEmail(request);

            verify(emailVerificationTokenRepository).deleteByToken("valid_token");
            verify(userRepository).save(confirmedUser);
        }

        @Test
        void verifyEmail_ThrowsException_WhenEmailAlreadyConfirmed() {
            VerifyEmailRequest request = new VerifyEmailRequest("valid_token");
            UUID userId = UUID.randomUUID();
            EmailVerificationToken token =
                    new EmailVerificationToken(
                            UUID.randomUUID(),
                            userId,
                            "valid_token",
                            Instant.now().plusSeconds(600),
                            Instant.now());
            User user =
                    new User(userId, "user", "mail@mail.com", "hash", true); // already confirmed

            when(emailVerificationTokenRepository.findByToken("valid_token"))
                    .thenReturn(Optional.of(token));
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));

            assertThatThrownBy(() -> authService.verifyEmail(request))
                    .isInstanceOf(EmailAlreadyConfirmedException.class);
        }
    }
}
