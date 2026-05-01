/* (C)2026 */
package com.worldquiz.repository;

import com.worldquiz.entities.RefreshToken;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RefreshTokenRepository extends MongoRepository<RefreshToken, UUID> {
    Optional<RefreshToken> findByToken(String token);

    void deleteByUserId(UUID userId);
}
