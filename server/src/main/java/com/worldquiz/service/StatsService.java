/* (C)2026 */
package com.worldquiz.service;

import com.worldquiz.dto.*;
import com.worldquiz.entities.*;
import com.worldquiz.repository.CardStatUserRepository;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatsService {
    private final CardStatUserRepository cardStatUserRepository;
    private final PlaceService placeService;

    public List<CardStatDto> getCardStats(List<CardStat> cardStats) {
        return cardStats.stream()
                .map(
                        c -> {
                            Place place = this.placeService.getPlace(c.placeId());
                            return place == null
                                    ? null
                                    : new CardStatDto(
                                            place.name(),
                                            c.category(),
                                            c.guessedRight(),
                                            c.guessedWrong());
                        })
                .filter(Objects::nonNull)
                .toList();
    }

    public void updateStats(List<CardStat> cardStats, User user) {
        List<CardStatUser> existingStats = cardStatUserRepository.findAllByUserid(user.id());

        record CardStatKey(int placeId, Category category) {}
        Map<CardStatKey, CardStatUser> existingMap =
                existingStats.stream()
                        .collect(
                                Collectors.toMap(
                                        stat -> new CardStatKey(stat.placeId(), stat.category()),
                                        Function.identity()));

        List<CardStatUser> updatedStats = new ArrayList<>();

        for (CardStat cardStat : cardStats) {
            CardStatKey key = new CardStatKey(cardStat.placeId(), cardStat.category());
            CardStatUser existing = existingMap.get(key);

            if (existing != null) {

                updatedStats.add(
                        CardStatUser.builder()
                                .id(existing.id())
                                .userid(existing.userid())
                                .placeId(existing.placeId())
                                .category(existing.category())
                                .guessedRight(existing.guessedRight() + cardStat.guessedRight())
                                .guessedWrong(existing.guessedWrong() + cardStat.guessedWrong())
                                .build());

            } else {

                updatedStats.add(
                        CardStatUser.builder()
                                .id(UUID.randomUUID())
                                .userid(user.id())
                                .placeId(cardStat.placeId())
                                .category(cardStat.category())
                                .guessedRight(cardStat.guessedRight())
                                .guessedWrong(cardStat.guessedWrong())
                                .build());
            }
        }

        cardStatUserRepository.saveAll(updatedStats);
    }
}
