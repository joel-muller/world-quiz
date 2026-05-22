/* (C)2026 */
package com.worldquiz.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.worldquiz.entities.User;
import io.jsonwebtoken.Claims;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class JwtServiceTest {

    private static final String SECRET = "verySecretKeyVerySecretKeyVerySecretKey123";

    private JwtService jwtService;

    private User user;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService(SECRET);

        user = mock(User.class);
        when(user.id()).thenReturn(UUID.randomUUID());
    }

    @Test
    void shouldGenerateValidAccessToken() {
        String token = jwtService.generateAccessToken(user);

        Claims claims = jwtService.parseToken(token);

        assertThat(claims.getSubject()).isEqualTo(user.id().toString());

        assertThat(claims.getIssuedAt()).isBeforeOrEqualTo(new Date());
        assertThat(claims.getExpiration()).isAfter(new Date());
    }

    @Test
    void shouldGenerateValidRefreshToken() {
        String token = jwtService.generateRefreshToken(user);

        Claims claims = jwtService.parseToken(token);

        assertThat(claims.getSubject()).isEqualTo(user.id().toString());

        assertThat(claims.getExpiration()).isAfter(claims.getIssuedAt());
    }

    @Test
    void shouldReturnRefreshTokenExpirationInstantAroundSevenDays() {
        Instant before = Instant.now();

        Instant expiration = jwtService.getRefreshTokenExpirationInstant();

        Instant after = Instant.now();

        Duration minDuration = Duration.between(before, expiration);
        Duration maxDuration = Duration.between(after, expiration);

        assertThat(minDuration.toDays()).isGreaterThanOrEqualTo(6);
        assertThat(maxDuration.toDays()).isLessThanOrEqualTo(7);
    }

    @Test
    void shouldParseGeneratedToken() {
        String token = jwtService.generateAccessToken(user);

        Claims claims = jwtService.parseToken(token);

        assertThat(claims.getSubject()).isEqualTo(user.id().toString());

        assertThat(claims.getIssuedAt()).isNotNull();
        assertThat(claims.getExpiration()).isNotNull();
    }

    @Test
    void shouldUseConfiguredSecretKey() {
        assertThat(jwtService.getKey()).isNotNull();
        assertThat(jwtService.getKey().getAlgorithm()).isEqualTo("HmacSHA256");
    }
}
