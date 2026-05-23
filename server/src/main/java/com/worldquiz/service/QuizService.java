/* (C)2026 */
package com.worldquiz.service;

import com.worldquiz.dto.*;
import com.worldquiz.entities.*;
import com.worldquiz.exceptions.QuizBelongsNotToUserException;
import com.worldquiz.exceptions.QuizNotFoundException;
import com.worldquiz.repository.QuizRepository;
import java.time.Instant;
import java.util.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuizService {
    private final CardService cardService;
    private final QuizRepository quizRepository;
    private final StatsService statsService;

    public QuizDto createGame(CreateQuizRequest createQuizRequest, User user) {
        log.info("Creating quiz for user {} with {} cards", user.id(), createQuizRequest.number());
        List<CardDto> cards =
                cardService.getCards(
                        createQuizRequest.number(),
                        createQuizRequest.categories(),
                        createQuizRequest.tags());
        log.debug("Fetched {} cards for quiz {}", cards.size(), user.id());
        QuizDto quizDto = new QuizDto(UUID.randomUUID(), cards);
        List<CardStat> cardStats = cards.stream().map(CardStat::fromDto).toList();
        Quiz quiz = new Quiz(quizDto.id(), user.id(), cardStats, Instant.now(), null);
        quizRepository.save(quiz);
        log.info("Quiz {} created for user {}", quiz.id(), user.id());
        return quizDto;
    }

    public QuizStatDto finishGame(FinishQuizRequest request, User user) {
        log.info("Finishing quiz {} for user {}", request.id(), user.id());
        Quiz quiz =
                quizRepository
                        .findById(request.id())
                        .orElseThrow(
                                () -> {
                                    log.warn("Quiz not found: {}", request.id());
                                    return new QuizNotFoundException(
                                            "Quiz not found with the id: " + request.id());
                                });

        if (!quiz.userId().equals(user.id())) {
            log.warn(
                    "User {} attempted to access quiz {} owned by {}",
                    user.id(),
                    quiz.id(),
                    quiz.userId());

            throw new QuizBelongsNotToUserException(
                    "Quiz doesn't belong to user with the id: " + quiz.id());
        }

        List<CardStat> updatedCards =
                quiz.cards().stream()
                        .map(
                                existingCard -> {
                                    Optional<CardStat> updated =
                                            request.cards().stream()
                                                    .filter(
                                                            c ->
                                                                    c.placeId()
                                                                            == existingCard
                                                                                    .placeId())
                                                    .findFirst()
                                                    .map(
                                                            c -> {
                                                                log.debug(
                                                                        "Updating card {} right={}"
                                                                                + " wrong={}",
                                                                        c.placeId(),
                                                                        c.guessedRight(),
                                                                        c.guessedWrong());
                                                                return existingCard.withGuesses(
                                                                        c.guessedRight(),
                                                                        c.guessedWrong());
                                                            });
                                    return updated.orElse(existingCard);
                                })
                        .toList();

        Quiz updatedQuiz =
                new Quiz(quiz.id(), quiz.userId(), updatedCards, quiz.createdAt(), Instant.now());

        List<CardStatDto> stats = statsService.getCardStats(updatedCards);

        statsService.updateStats(updatedCards, user);
        quizRepository.save(updatedQuiz);
        log.info("Quiz {} successfully finished", quiz.id());
        return new QuizStatDto(request.id(), "Well done", stats);
    }
}
