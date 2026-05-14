/* (C)2026 */
package com.worldquiz.repository;

import com.worldquiz.entities.EmailSendLog;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EmailSendLogRepository extends MongoRepository<EmailSendLog, UUID> {
    long countBySentAtBetween(Instant start, Instant end);

    long countByUserIdAndSentAtBetween(UUID userId, Instant start, Instant end);

    Optional<EmailSendLog> findTopByUserIdOrderBySentAtDesc(UUID userId);
}
