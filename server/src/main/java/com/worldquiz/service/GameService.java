/* (C)2026 */
package com.worldquiz.service;

import com.worldquiz.dto.CreateQuizRequest;
import com.worldquiz.dto.FinishGameRequest;
import com.worldquiz.dto.GameStatResponse;
import com.worldquiz.entities.*;
import java.util.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class GameService {
    private final CardService cardService;
    private final Map<UUID, Quiz> games;

    public Quiz createGame(CreateQuizRequest createQuizRequest, User user) {
        List<Card> cards =
                cardService.getCards(
                        createQuizRequest.number(),
                        createQuizRequest.categories(),
                        createQuizRequest.tags());
        Quiz quiz = new Quiz(UUID.randomUUID(), cards);
        games.put(quiz.id(), quiz);
        return quiz;
    }

    public GameStatResponse finishGame(FinishGameRequest request) {
        games.remove(request.id());
        return new GameStatResponse(request.id(), "Well done");
    }
}
