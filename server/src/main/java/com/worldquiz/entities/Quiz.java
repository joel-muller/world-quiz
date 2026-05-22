/* (C)2026 */
package com.worldquiz.entities;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("quizzes")
public record Quiz(
        UUID id, UUID userId, List<CardStat> cards, Instant createdAt, Instant finishedAt) {}
