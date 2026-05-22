/* (C)2026 */
package com.worldquiz.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import com.worldquiz.entities.CardStat;
import com.worldquiz.entities.CardStatUser;
import com.worldquiz.entities.Category;
import com.worldquiz.entities.User;
import com.worldquiz.repository.CardStatUserRepository;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StatsServiceTest {

    @Mock private CardStatUserRepository cardStatUserRepository;

    @InjectMocks private StatsService statsService;

    @Captor private ArgumentCaptor<List<CardStatUser>> statsCaptor;

    @Test
    void shouldCreateNewStatsWhenNoneExist() {
        UUID userId = UUID.randomUUID();

        User user = mock(User.class);
        when(user.id()).thenReturn(userId);

        CardStat stat =
                CardStat.builder()
                        .placeId(33)
                        .category(Category.FLAG_NAME)
                        .guessedRight(4)
                        .guessedWrong(1)
                        .build();

        when(cardStatUserRepository.findAllByUserid(userId)).thenReturn(List.of());

        statsService.updateStats(List.of(stat), user);

        verify(cardStatUserRepository).saveAll(statsCaptor.capture());

        List<CardStatUser> saved = statsCaptor.getValue();

        assertThat(saved).hasSize(1);

        CardStatUser result = saved.getFirst();

        assertThat(result.id()).isNotNull();
        assertThat(result.userid()).isEqualTo(userId);
        assertThat(result.placeId()).isEqualTo(33);
        assertThat(result.category()).isEqualTo(Category.FLAG_NAME);
        assertThat(result.guessedRight()).isEqualTo(4);
        assertThat(result.guessedWrong()).isEqualTo(1);
    }

    @Test
    void shouldUpdateExistingStats() {
        UUID userId = UUID.randomUUID();
        UUID statId = UUID.randomUUID();

        User user = mock(User.class);
        when(user.id()).thenReturn(userId);

        CardStat incoming =
                CardStat.builder()
                        .placeId(7)
                        .category(Category.CAPITAL_NAME)
                        .guessedRight(3)
                        .guessedWrong(2)
                        .build();

        CardStatUser existing =
                CardStatUser.builder()
                        .id(statId)
                        .userid(userId)
                        .placeId(7)
                        .category(Category.CAPITAL_NAME)
                        .guessedRight(10)
                        .guessedWrong(1)
                        .build();

        when(cardStatUserRepository.findAllByUserid(userId)).thenReturn(List.of(existing));

        statsService.updateStats(List.of(incoming), user);

        verify(cardStatUserRepository).saveAll(statsCaptor.capture());

        CardStatUser updated = statsCaptor.getValue().getFirst();

        assertThat(updated.id()).isEqualTo(statId);
        assertThat(updated.userid()).isEqualTo(userId);
        assertThat(updated.placeId()).isEqualTo(7);
        assertThat(updated.category()).isEqualTo(Category.CAPITAL_NAME);
        assertThat(updated.guessedRight()).isEqualTo(13);
        assertThat(updated.guessedWrong()).isEqualTo(3);
    }

    @Test
    void shouldMatchStatsByPlaceIdAndCategory() {
        UUID userId = UUID.randomUUID();

        User user = mock(User.class);
        when(user.id()).thenReturn(userId);

        CardStat incoming =
                CardStat.builder()
                        .placeId(5)
                        .category(Category.FLAG_NAME)
                        .guessedRight(2)
                        .guessedWrong(0)
                        .build();

        CardStatUser existingDifferentCategory =
                CardStatUser.builder()
                        .id(UUID.randomUUID())
                        .userid(userId)
                        .placeId(5)
                        .category(Category.MAP_NAME)
                        .guessedRight(8)
                        .guessedWrong(1)
                        .build();

        when(cardStatUserRepository.findAllByUserid(userId))
                .thenReturn(List.of(existingDifferentCategory));

        statsService.updateStats(List.of(incoming), user);

        verify(cardStatUserRepository).saveAll(statsCaptor.capture());

        CardStatUser saved = statsCaptor.getValue().getFirst();

        assertThat(saved.id()).isNotEqualTo(existingDifferentCategory.id());
        assertThat(saved.userid()).isEqualTo(userId);
        assertThat(saved.placeId()).isEqualTo(5);
        assertThat(saved.category()).isEqualTo(Category.FLAG_NAME);
        assertThat(saved.guessedRight()).isEqualTo(2);
        assertThat(saved.guessedWrong()).isZero();
    }

    @Test
    void shouldSaveMultipleUpdatedStats() {
        UUID userId = UUID.randomUUID();

        User user = mock(User.class);
        when(user.id()).thenReturn(userId);

        CardStat france =
                CardStat.builder()
                        .placeId(1)
                        .category(Category.FLAG_NAME)
                        .guessedRight(1)
                        .guessedWrong(0)
                        .build();

        CardStat japan =
                CardStat.builder()
                        .placeId(81)
                        .category(Category.CAPITAL_NAME)
                        .guessedRight(0)
                        .guessedWrong(2)
                        .build();

        when(cardStatUserRepository.findAllByUserid(userId)).thenReturn(List.of());

        statsService.updateStats(List.of(france, japan), user);

        verify(cardStatUserRepository).saveAll(statsCaptor.capture());

        assertThat(statsCaptor.getValue()).hasSize(2);
    }
}
