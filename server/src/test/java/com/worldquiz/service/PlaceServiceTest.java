/* (C)2026 */
package com.worldquiz.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.worldquiz.entities.Category;
import com.worldquiz.entities.Place;
import com.worldquiz.entities.Tag;
import com.worldquiz.reader.PlaceReader;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PlaceServiceTest {

    @Mock private PlaceReader placeReader;

    @InjectMocks private PlaceService placeService;

    @Test
    void shouldReturnPlacesMatchingCategoryAndTag() {
        Place france =
                Place.builder()
                        .flag("france-flag")
                        .tags(List.of(Tag.EUROPE, Tag.EUROPEAN_UNION))
                        .build();

        Place atlanticOcean = Place.builder().flag(null).tags(List.of(Tag.OCEANS_AND_SEAS)).build();

        Place japan = Place.builder().flag("japan-flag").tags(List.of(Tag.ASIA)).build();

        when(placeReader.getPlaces()).thenReturn(List.of(france, atlanticOcean, japan));

        List<Place> result =
                placeService.getPlacesByTagsAndCategories(Category.FLAG_NAME, List.of(Tag.EUROPE));

        assertThat(result).containsExactly(france);
    }

    @Test
    void shouldReturnPlacesWithAtLeastOneMatchingTag() {
        Place egypt =
                Place.builder().capital("Cairo").tags(List.of(Tag.AFRICA, Tag.MIDDLE_EAST)).build();

        Place thailand =
                Place.builder().capital("Bangkok").tags(List.of(Tag.SOUTHEAST_ASIA)).build();

        when(placeReader.getPlaces()).thenReturn(List.of(egypt, thailand));

        List<Place> result =
                placeService.getPlacesByTagsAndCategories(
                        Category.CAPITAL_NAME, List.of(Tag.MIDDLE_EAST, Tag.SOUTHEAST_ASIA));

        assertThat(result).containsExactlyInAnyOrder(egypt, thailand);
    }

    @Test
    void shouldReturnEmptyListWhenNoPlacesMatch() {
        Place brazil = Place.builder().capital(null).tags(List.of(Tag.SOUTH_AMERICA)).build();

        when(placeReader.getPlaces()).thenReturn(List.of(brazil));

        List<Place> result =
                placeService.getPlacesByTagsAndCategories(
                        Category.CAPITAL_NAME, List.of(Tag.EUROPE));

        assertThat(result).isEmpty();
    }

    @Test
    void shouldFilterByMapCategory() {
        Place europeMap = Place.builder().maps("europe-map").tags(List.of(Tag.EUROPE)).build();

        Place caribbean = Place.builder().maps(null).tags(List.of(Tag.CARIBBEAN)).build();

        when(placeReader.getPlaces()).thenReturn(List.of(europeMap, caribbean));

        List<Place> result =
                placeService.getPlacesByTagsAndCategories(Category.MAP_NAME, List.of(Tag.EUROPE));

        assertThat(result).containsExactly(europeMap);
    }

    @Test
    void shouldSupportNameCapitalCategory() {
        Place canada = Place.builder().capital("Ottawa").tags(List.of(Tag.NORTH_AMERICA)).build();

        Place pacific = Place.builder().capital(null).tags(List.of(Tag.OCEANS_AND_SEAS)).build();

        when(placeReader.getPlaces()).thenReturn(List.of(canada, pacific));

        List<Place> result =
                placeService.getPlacesByTagsAndCategories(
                        Category.NAME_CAPITAL, List.of(Tag.NORTH_AMERICA));

        assertThat(result).containsExactly(canada);
    }
}
