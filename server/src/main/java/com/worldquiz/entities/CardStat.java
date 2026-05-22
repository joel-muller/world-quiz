/* (C)2026 */
package com.worldquiz.entities;

import com.worldquiz.dto.CardDto;
import lombok.Builder;

@Builder
public record CardStat(int placeId, Category category, int guessedRight, int guessedWrong) {

    public static CardStat fromDto(CardDto cardDto) {
        return new CardStat(
                cardDto.placeId(),
                cardDto.category(),
                cardDto.guessedRight(),
                cardDto.guessedWrong());
    }

    public CardStat withGuesses(int guessedRight, int guessedWrong) {
        return new CardStat(placeId, category, guessedRight, guessedWrong);
    }
}
