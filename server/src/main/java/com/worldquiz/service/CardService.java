/* (C)2026 */
package com.worldquiz.service;

import com.worldquiz.entities.Card;
import com.worldquiz.entities.Category;
import com.worldquiz.entities.Place;
import com.worldquiz.entities.Tag;
import com.worldquiz.reader.PlaceReader;
import java.util.*;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CardService {
    private final PlaceReader placeReader;

    public List<Card> getCards(int numberOfCards, List<Category> categories, Set<Tag> tags) {
        Objects.requireNonNull(categories);
        Objects.requireNonNull(tags);

        List<Card> allCards =
                new ArrayList<>(
                        categories.stream()
                                .flatMap(
                                        category ->
                                                this.placeReader.getPlaces().stream()
                                                        .filter(
                                                                place ->
                                                                        isCategory(place, category)
                                                                                && hasAtLeastOneTag(
                                                                                        place,
                                                                                        tags))
                                                        .map(place -> getCard(place, category)))
                                .toList());

        Collections.shuffle(allCards);

        return allCards.stream().limit(numberOfCards).toList();
    }

    private static Card getCard(Place place, Category category) {
        return switch (category) {
            case MAP_NAME ->
                    new Card(
                            place.id(),
                            category,
                            place.maps(),
                            null,
                            place.nameWithCapital(),
                            mergeInfos(Arrays.asList(place.placeInfo(), place.capitalInfo())));
            case FLAG_NAME ->
                    new Card(
                            place.id(),
                            category,
                            place.flag(),
                            null,
                            place.nameWithCapital(),
                            mergeInfos(
                                    Arrays.asList(
                                            place.placeInfo(),
                                            place.capitalInfo(),
                                            place.flagInfo())));
            case CAPITAL_NAME ->
                    new Card(
                            place.id(),
                            category,
                            place.capital(),
                            place.capitalInfo(),
                            place.name(),
                            place.placeInfo());
            case NAME_CAPITAL ->
                    new Card(
                            place.id(),
                            category,
                            place.name(),
                            place.placeInfo(),
                            place.capital(),
                            place.capitalInfo());
        };
    }

    private static String mergeInfos(List<String> infos) {
        String info = infos.stream().filter(Objects::nonNull).collect(Collectors.joining(" "));
        return info.isEmpty() ? null : info;
    }

    private static boolean isCategory(Place place, Category category) {
        return switch (category) {
            case MAP_NAME -> place.maps() != null;
            case FLAG_NAME -> place.flag() != null;
            case CAPITAL_NAME, NAME_CAPITAL -> place.capital() != null;
        };
    }

    private static boolean hasAtLeastOneTag(Place place, Set<Tag> tags) {
        return place.tags().stream().anyMatch(tags::contains);
    }
}
