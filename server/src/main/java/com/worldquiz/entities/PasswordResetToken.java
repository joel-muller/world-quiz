/* (C)2026 */
package com.worldquiz.entities;

import java.time.Instant;
import java.util.UUID;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("password_reset_tokens")
public record PasswordResetToken(
        @Id UUID id, UUID userId, String token, Instant expiresAt, Instant createdAt) {}
