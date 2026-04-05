/* (C)2026 */
package com.worldquiz.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import com.worldquiz.entities.*;
import com.worldquiz.reader.PlaceReader;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class CardServiceTest {
    private CardService cardService;

    @BeforeEach
    void setUp() {
        PlaceReader placeReader = Mockito.mock(PlaceReader.class);
        cardService = new CardService(placeReader);

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

        when(placeReader.getPlaces()).thenReturn(List.of(place1, place2));
    }

    @Test
    void testGetCards_limitNumberOfCards() {
        List<Card> cards = cardService.getCards(1, List.of(Category.MAP_NAME), Set.of(Tag.EUROPE));
        assertEquals(1, cards.size());
    }

    @Test
    void testGetCards_filtersByCategory() {
        List<Card> cards = cardService.getCards(10, List.of(Category.MAP_NAME), Set.of(Tag.EUROPE));
        assertTrue(cards.stream().allMatch(card -> card.front() != null));
        // place2.maps is null, so should be excluded
        assertTrue(cards.stream().allMatch(card -> card.placeId() == 1));
    }

    @Test
    void testGetCards_filtersByTag() {
        Set<Tag> tagFilter = Set.of(Tag.SOVEREIGN_STATE);
        List<Card> cards = cardService.getCards(10, List.of(Category.FLAG_NAME), tagFilter);
        assertEquals(1, cards.size());
        assertEquals(2, cards.get(0).placeId());
    }

    @Test
    void testGetCards_mergeInfosCorrectlyForMapName() {
        List<Card> cards = cardService.getCards(10, List.of(Category.MAP_NAME), Set.of(Tag.EUROPE));
        Card card = cards.get(0);
        assertEquals("Info1 CapitalInfo1", card.infoBack());
    }

    @Test
    void testGetCards_mergeInfosCorrectlyForFlagName() {
        List<Card> cards =
                cardService.getCards(10, List.of(Category.FLAG_NAME), Set.of(Tag.EUROPE));
        // place1 and place2 have flags, both tags include EUROPE
        Card card = cards.stream().filter(c -> c.placeId() == 1).findFirst().orElseThrow();
        assertEquals("Info1 CapitalInfo1 FlagInfo1", card.infoBack());
    }

    @Test
    void testGetCards_categoryCapitalName() {
        List<Card> cards =
                cardService.getCards(10, List.of(Category.CAPITAL_NAME), Set.of(Tag.EUROPE));
        Card card = cards.stream().filter(c -> c.placeId() == 1).findFirst().orElseThrow();
        assertEquals("London", card.front());
        assertEquals("CapitalInfo1", card.infoFront());
        assertEquals("England", card.back());
        assertEquals("Info1", card.infoBack());
    }

    @Test
    void testGetCards_categoryNameCapital() {
        List<Card> cards =
                cardService.getCards(10, List.of(Category.NAME_CAPITAL), Set.of(Tag.EUROPE));
        Card card = cards.stream().filter(c -> c.placeId() == 1).findFirst().orElseThrow();
        assertEquals("England", card.front());
        assertEquals("Info1", card.infoFront());
        assertEquals("London", card.back());
        assertEquals("CapitalInfo1", card.infoBack());
    }

    @Test
    void testGetCards_emptyResultWhenNoTagMatch() {
        Set<Tag> tagFilter = Set.of(Tag.ASIA);
        List<Card> cards = cardService.getCards(10, List.of(Category.FLAG_NAME), tagFilter);
        assertTrue(cards.isEmpty());
    }

    @Test
    void testGetCards_emptyCategoriesThrows() {
        assertThrows(
                NullPointerException.class,
                () -> cardService.getCards(1, null, Set.of(Tag.EUROPE)));
    }

    @Test
    void testGetCards_emptyTagsThrows() {
        assertThrows(
                NullPointerException.class,
                () -> cardService.getCards(1, List.of(Category.FLAG_NAME), null));
    }
}
