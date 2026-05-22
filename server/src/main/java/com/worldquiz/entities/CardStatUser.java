/* (C)2026 */
package com.worldquiz.entities;

import java.util.UUID;
import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Builder
@Document("card_stats_user")
public record CardStatUser(
        @Id UUID id,
        UUID userid,
        int placeId,
        Category category,
        int guessedRight,
        int guessedWrong) {}
