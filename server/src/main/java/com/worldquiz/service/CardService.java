/* (C)2026 */
package com.worldquiz.service;

import com.worldquiz.dto.CardDto;
import com.worldquiz.entities.Category;
import com.worldquiz.entities.Place;
import com.worldquiz.entities.Tag;
import java.util.*;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CardService {
    private final PlaceService placeService;

    public List<CardDto> getCards(
            Integer numberOfCards, List<Category> categories, List<Tag> tags) {
        Objects.requireNonNull(categories);
        Objects.requireNonNull(tags);

        List<CardDto> allCards =
                new ArrayList<>(
                        categories.stream()
                                .flatMap(
                                        category ->
                                                this.placeService
                                                        .getPlacesByTagsAndCategories(
                                                                category, tags)
                                                        .stream()
                                                        .map(place -> getCard(place, category)))
                                .toList());
        Collections.shuffle(allCards);

        return allCards.stream().limit(numberOfCards).toList();
    }

    private static CardDto getCard(Place place, Category category) {
        return switch (category) {
            case MAP_NAME ->
                    CardDto.builder()
                            .placeId(place.id())
                            .category(category)
                            .front(place.maps())
                            .back(place.nameWithCapital())
                            .infoBack(
                                    mergeInfos(
                                            Arrays.asList(place.placeInfo(), place.capitalInfo())))
                            .build();
            case FLAG_NAME ->
                    CardDto.builder()
                            .placeId(place.id())
                            .category(category)
                            .front(place.flag())
                            .back(place.nameWithCapital())
                            .infoBack(
                                    mergeInfos(
                                            Arrays.asList(
                                                    place.placeInfo(),
                                                    place.capitalInfo(),
                                                    place.flagInfo())))
                            .build();
            case CAPITAL_NAME ->
                    CardDto.builder()
                            .placeId(place.id())
                            .category(category)
                            .front(place.capital())
                            .infoFront(place.capitalInfo())
                            .back(place.name())
                            .infoBack(place.placeInfo())
                            .build();
            case NAME_CAPITAL ->
                    CardDto.builder()
                            .placeId(place.id())
                            .category(category)
                            .front(place.name())
                            .infoFront(place.placeInfo())
                            .back(place.capital())
                            .infoBack(place.capitalInfo())
                            .build();
        };
    }

    private static String mergeInfos(List<String> infos) {
        String info = infos.stream().filter(Objects::nonNull).collect(Collectors.joining(" "));
        return info.isEmpty() ? null : info;
    }
}
