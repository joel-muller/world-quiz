/* (C)2026 */
package com.worldquiz.service;

import com.worldquiz.entities.Category;
import com.worldquiz.entities.Place;
import com.worldquiz.entities.Tag;
import com.worldquiz.reader.PlaceReader;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PlaceService {
    private final PlaceReader placeReader;

    public Place getPlace(int placeId) {
        return this.placeReader.getPlaces().stream()
                .filter(p -> p.id() == placeId)
                .findFirst()
                .orElse(null);
    }

    public List<Place> getPlacesByTagsAndCategories(Category category, List<Tag> tags) {
        return this.placeReader.getPlaces().stream()
                .filter(place -> isCategory(place, category) && hasAtLeastOneTag(place, tags))
                .toList();
    }

    private static boolean isCategory(Place place, Category category) {
        return switch (category) {
            case MAP_NAME -> place.maps() != null;
            case FLAG_NAME -> place.flag() != null;
            case CAPITAL_NAME, NAME_CAPITAL -> place.capital() != null;
        };
    }

    private static boolean hasAtLeastOneTag(Place place, List<Tag> tags) {
        Set<Tag> tagsSet = Set.copyOf(tags);
        return place.tags().stream().anyMatch(tagsSet::contains);
    }
}
