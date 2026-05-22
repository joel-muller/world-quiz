/* (C)2026 */
package com.worldquiz.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.worldquiz.dto.CardDto;
import com.worldquiz.entities.*;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CardServiceTest {
    private CardService cardService;

    @BeforeEach
    void setUp() {
        PlaceService placeService = mock(PlaceService.class);
        cardService = new CardService(placeService);

        Place place1 =
                Place.builder()
                        .id(1)
                        .name("England")
                        .capital("London")
                        .placeInfo("Info1")
                        .capitalInfo("CapitalInfo1")
                        .flag("Flag1")
                        .maps("Map1")
                        .flagInfo("FlagInfo1")
                        .tags(List.of(Tag.EUROPE))
                        .build();

        Place place2 =
                Place.builder()
                        .id(2)
                        .name("France")
                        .capital("Paris")
                        .placeInfo("Info2")
                        .capitalInfo("CapitalInfo2")
                        .flag("Flag2")
                        .maps(null)
                        .flagInfo("FlagInfo2")
                        .tags(List.of(Tag.EUROPE, Tag.SOVEREIGN_STATE))
                        .build();

        when(placeService.getPlacesByTagsAndCategories(any(), anyList()))
                .thenReturn(List.of(place1, place2));
    }

    @Test
    void testGetCards_limitNumberOfCards() {
        List<CardDto> cards =
                cardService.getCards(1, List.of(Category.MAP_NAME), List.of(Tag.EUROPE));
        assertEquals(1, cards.size());
    }

    @Test
    void testGetCards_mergeInfosCorrectlyForFlagName() {
        List<CardDto> cards =
                cardService.getCards(10, List.of(Category.FLAG_NAME), List.of(Tag.EUROPE));
        CardDto card = cards.stream().filter(c -> c.placeId() == 1).findFirst().orElseThrow();
        assertEquals("Info1 CapitalInfo1 FlagInfo1", card.infoBack());
    }

    @Test
    void testGetCards_categoryCapitalName() {
        List<CardDto> cards =
                cardService.getCards(10, List.of(Category.CAPITAL_NAME), List.of(Tag.EUROPE));
        CardDto card = cards.stream().filter(c -> c.placeId() == 1).findFirst().orElseThrow();
        assertEquals("London", card.front());
        assertEquals("CapitalInfo1", card.infoFront());
        assertEquals("England", card.back());
        assertEquals("Info1", card.infoBack());
    }

    @Test
    void testGetCards_categoryNameCapital() {
        List<CardDto> cards =
                cardService.getCards(10, List.of(Category.NAME_CAPITAL), List.of(Tag.EUROPE));
        CardDto card = cards.stream().filter(c -> c.placeId() == 1).findFirst().orElseThrow();
        assertEquals("England", card.front());
        assertEquals("Info1", card.infoFront());
        assertEquals("London", card.back());
        assertEquals("CapitalInfo1", card.infoBack());
    }

    @Test
    void testGetCards_emptyCategoriesThrows() {
        assertThrows(
                NullPointerException.class,
                () -> cardService.getCards(1, null, List.of(Tag.EUROPE)));
    }

    @Test
    void testGetCards_emptyTagsThrows() {
        assertThrows(
                NullPointerException.class,
                () -> cardService.getCards(1, List.of(Category.FLAG_NAME), null));
    }
}
