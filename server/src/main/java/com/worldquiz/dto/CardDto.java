/* (C)2026 */
package com.worldquiz.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.worldquiz.entities.Category;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record CardDto(
        int placeId,
        Category category,
        String front,
        String infoFront,
        String back,
        String infoBack,
        int guessedRight,
        int guessedWrong) {
    public CardDto {
        guessedRight = Math.max(guessedRight, 0);
        guessedWrong = Math.max(guessedWrong, 0);
    }
}
