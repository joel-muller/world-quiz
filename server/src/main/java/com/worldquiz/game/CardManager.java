package com.worldquiz.game;

import com.worldquiz.entities.Card;
import com.worldquiz.entities.Category;
import com.worldquiz.entities.Place;
import com.worldquiz.entities.Tag;

import java.util.List;
import java.util.Objects;
import java.util.Set;

public class CardManager {
    private final List<Place> places;

    public CardManager(List<Place> places) {
        this.places = places;
    }

    public List<Card> getCards(int numberOfCards, Category category, Set<Tag> tags) {
        Objects.requireNonNull(category);
        Objects.requireNonNull(tags);
        return this.places.stream().filter(p -> isCategory(p, category) && hasAtLeastOneTag(p, tags)).map(p -> getCard(p, category)).limit(numberOfCards).toList();
    }

    private static Card getCard(Place place, Category category) {
        return switch (category) {
            case MAP_NAME -> new Card(place.id(), category, place.maps(), "", place.nameWithCapital(), place.placeInfo() + " " + place.capitalInfo());
            case FLAG_NAME -> new Card(place.id(), category, place.flag(), "", place.nameWithCapital(), place.placeInfo() + " " + place.capitalInfo() + " " + place.flagInfo());
            case CAPITAL_NAME -> new Card(place.id(), category, place.capital(), place.capital(), place.name(), place.placeInfo());
            case NAME_CAPITAL -> new Card(place.id(), category, place.name(), place.placeInfo(), place.capital(), place.capitalInfo());
        };
    }


    private static boolean isCategory(Place place, Category category) {
        return switch (category) {
            case MAP_NAME -> place.maps() != null;
            case FLAG_NAME -> place.flag() != null;
            case CAPITAL_NAME, NAME_CAPITAL -> place.capital() != null;
        };
    }

    private static boolean hasAtLeastOneTag(Place place, Set<Tag> tags)  {
        return place.tags().stream().anyMatch(tags::contains);
    }
}
