/* (C)2026 */
package com.worldquiz.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.worldquiz.dto.*;
import com.worldquiz.entities.*;
import com.worldquiz.exceptions.QuizBelongsNotToUserException;
import com.worldquiz.exceptions.QuizNotFoundException;
import com.worldquiz.repository.QuizRepository;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GameServiceTest {
    @Mock private CardService cardService;
    @Mock private QuizRepository quizRepository;
    @Mock private StatsService statsService;

    @InjectMocks private GameService gameService;

    private User user;

    @BeforeEach
    void setUp() {
        user = mock(User.class);
        when(user.id()).thenReturn(UUID.randomUUID());
    }

    @Test
    void createGame_shouldCreateAndPersistQuiz() {
        CreateQuizRequest request =
                new CreateQuizRequest(List.of(Category.FLAG_NAME), List.of(Tag.EUROPE), 2);

        List<CardDto> cards = List.of(card(1, 0, 0), card(2, 0, 0));

        when(cardService.getCards(2, request.categories(), request.tags())).thenReturn(cards);

        QuizDto result = gameService.createGame(request, user);

        assertThat(result).isNotNull();
        assertThat(result.cards()).hasSize(2);

        ArgumentCaptor<Quiz> quizCaptor = ArgumentCaptor.forClass(Quiz.class);

        verify(quizRepository).save(quizCaptor.capture());

        Quiz savedQuiz = quizCaptor.getValue();

        assertThat(savedQuiz.userId()).isEqualTo(user.id());
        assertThat(savedQuiz.cards()).hasSize(2);
        assertThat(savedQuiz.finishedAt()).isNull();
        assertThat(savedQuiz.createdAt()).isNotNull();
    }

    @Test
    void finishGame_shouldUpdateQuizAndStats() {
        UUID quizId = UUID.randomUUID();

        CardStat existingCard = new CardStat(1, Category.MAP_NAME, 1, 0);
        Quiz quiz = new Quiz(quizId, user.id(), List.of(existingCard), Instant.now(), null);

        FinishGameRequest request = new FinishGameRequest(quizId, List.of(card(1, 3, 1)));

        when(quizRepository.findById(quizId)).thenReturn(Optional.of(quiz));

        GameStatDto result = gameService.finishGame(request, user);

        assertThat(result.id()).isEqualTo(quizId);
        assertThat(result.info()).isEqualTo("Well done");

        ArgumentCaptor<Quiz> quizCaptor = ArgumentCaptor.forClass(Quiz.class);

        verify(quizRepository).save(quizCaptor.capture());
        verify(statsService).updateStats(any(), eq(user));

        Quiz savedQuiz = quizCaptor.getValue();

        assertThat(savedQuiz.finishedAt()).isNotNull();
        assertThat(savedQuiz.cards())
                .singleElement()
                .satisfies(
                        card -> {
                            assertThat(card.guessedRight()).isEqualTo(3);
                            assertThat(card.guessedWrong()).isEqualTo(1);
                        });
    }

    @Test
    void finishGame_shouldThrowWhenQuizNotFound() {
        UUID quizId = UUID.randomUUID();

        FinishGameRequest request = new FinishGameRequest(quizId, List.of());

        when(quizRepository.findById(quizId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> gameService.finishGame(request, user))
                .isInstanceOf(QuizNotFoundException.class)
                .hasMessage("Quiz not found with the id: " + quizId);

        verify(quizRepository, never()).save(any());
        verify(statsService, never()).updateStats(any(), any());
    }

    @Test
    void finishGame_shouldThrowWhenQuizBelongsToAnotherUser() {
        UUID quizId = UUID.randomUUID();

        User anotherUser = mock(User.class);
        when(anotherUser.id()).thenReturn(UUID.randomUUID());

        Quiz quiz = new Quiz(quizId, anotherUser.id(), List.of(), Instant.now(), null);

        FinishGameRequest request = new FinishGameRequest(quizId, List.of());

        when(quizRepository.findById(quizId)).thenReturn(Optional.of(quiz));

        assertThatThrownBy(() -> gameService.finishGame(request, user))
                .isInstanceOf(QuizBelongsNotToUserException.class)
                .hasMessage("Quiz doesn't belong to user with the id: " + quizId);

        verify(quizRepository, never()).save(any());
        verify(statsService, never()).updateStats(any(), any());
    }

    @Test
    void finishGame_shouldKeepExistingCardWhenNoUpdateProvided() {
        UUID quizId = UUID.randomUUID();

        CardStat unchanged = new CardStat(1, Category.MAP_NAME, 2, 1);

        Quiz quiz = new Quiz(quizId, user.id(), List.of(unchanged), Instant.now(), null);

        FinishGameRequest request = new FinishGameRequest(quizId, List.of(card(999, 5, 5)));

        when(quizRepository.findById(quizId)).thenReturn(Optional.of(quiz));

        gameService.finishGame(request, user);

        ArgumentCaptor<Quiz> quizCaptor = ArgumentCaptor.forClass(Quiz.class);

        verify(quizRepository).save(quizCaptor.capture());

        Quiz savedQuiz = quizCaptor.getValue();

        assertThat(savedQuiz.cards())
                .singleElement()
                .satisfies(
                        card -> {
                            assertThat(card.guessedRight()).isEqualTo(2);
                            assertThat(card.guessedWrong()).isEqualTo(1);
                        });
    }

    private CardDto card(int placeId, int right, int wrong) {
        return CardDto.builder().placeId(placeId).guessedRight(right).guessedWrong(wrong).build();
    }
}
