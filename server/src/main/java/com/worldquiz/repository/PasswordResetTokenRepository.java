/* (C)2026 */
package com.worldquiz.repository;

import com.worldquiz.entities.PasswordResetToken;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PasswordResetTokenRepository extends MongoRepository<PasswordResetToken, UUID> {
    Optional<PasswordResetToken> findByToken(String token);

    void deleteByToken(String token);

    void deleteAllByUserId(UUID userId);
}
