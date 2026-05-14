/* (C)2026 */
package com.worldquiz.repository;

import com.worldquiz.entities.EmailVerificationToken;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EmailVerificationTokenRepository
        extends MongoRepository<EmailVerificationToken, UUID> {
    Optional<EmailVerificationToken> findByToken(String token);

    List<EmailVerificationToken> findAllByStoredBetween(Instant start, Instant end);

    void deleteAllByUserId(UUID userId);

    void deleteByToken(String token);
}
