package com.worldquiz.game;

import com.worldquiz.entities.Card;
import com.worldquiz.entities.Category;
import com.worldquiz.entities.Place;
import com.worldquiz.entities.Tag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class CardManagerTest {
    private Place france;
    private Place japan;
    private Place australia;
    private CardManager manager;

    @BeforeEach
    void setUp() {
        france = new Place(
                1,
                "France",
                "Some info about France",
                "Paris",
                "Capital info",
                "EU",
                "France map",
                "France flag",
                "Flag info",
                List.of(Tag.EUROPE, Tag.SOVEREIGN_STATE)
        );

        japan = new Place(
                2,
                "Japan",
                "Info about Japan",
                "Tokyo",
                "Capital info Japan",
                "AS",
                "Japan map",
                "Japan flag",
                "Flag info Japan",
                List.of(Tag.ASIA, Tag.SOVEREIGN_STATE)
        );

        australia = new Place(
                3,
                "Australia",
                "Info about Australia",
                "Canberra",
                "Capital info AU",
                "OC",
                "Australia map",
                "Australia flag",
                "Flag info AU",
                List.of(Tag.OCEANIA, Tag.SOVEREIGN_STATE)
        );

        manager = new CardManager(List.of(france, japan, australia));
    }

    @Test
    void testGetCards_MapNameCategory() {
        List<Card> cards = manager.getCards(10, Category.MAP_NAME, Set.of(Tag.SOVEREIGN_STATE));

        assertThat(cards).hasSize(3);
        assertThat(cards).allSatisfy(card -> {
            assertThat(card.category()).isEqualTo(Category.MAP_NAME);
            assertThat(card.front()).endsWith("map");
        });
    }

    @Test
    void testGetCards_FlagNameCategory() {
        List<Card> cards = manager.getCards(10, Category.FLAG_NAME, Set.of(Tag.EUROPE));

        assertThat(cards).hasSize(1);
        Card franceCard = cards.get(0);
        assertThat(franceCard.front()).isEqualTo("France flag");
        assertThat(franceCard.back()).contains("France (Paris)");
    }

    @Test
    void testGetCards_CapitalNameCategory() {
        List<Card> cards = manager.getCards(10, Category.CAPITAL_NAME, Set.of(Tag.ASIA));

        assertThat(cards).hasSize(1);
        Card japanCard = cards.get(0);
        assertThat(japanCard.front()).isEqualTo("Tokyo");
        assertThat(japanCard.back()).isEqualTo("Japan");
    }

    @Test
    void testGetCards_NameCapitalCategory() {
        List<Card> cards = manager.getCards(10, Category.NAME_CAPITAL, Set.of(Tag.OCEANIA));

        assertThat(cards).hasSize(1);
        Card australiaCard = cards.get(0);
        assertThat(australiaCard.front()).isEqualTo("Australia");
        assertThat(australiaCard.back()).isEqualTo("Canberra");
    }

    @Test
    void testGetCards_TagFiltering() {
        List<Card> cards = manager.getCards(10, Category.MAP_NAME, Set.of(Tag.EUROPE));

        assertThat(cards).hasSize(1);
        assertThat(cards.get(0).placeId()).isEqualTo(1);
    }

    @Test
    void testGetCards_LimitNumberOfCards() {
        List<Card> cards = manager.getCards(2, Category.MAP_NAME, Set.of(Tag.SOVEREIGN_STATE));

        assertThat(cards).hasSize(2);
    }

    @Test
    void testGetCards_NoMatchingTags() {
        List<Card> cards = manager.getCards(10, Category.MAP_NAME, Set.of(Tag.MEDITERRANEAN));

        assertThat(cards).isEmpty();
    }

    @Test
    void testGetCards_EmptyPlaces() {
        CardManager emptyManager = new CardManager(List.of());
        List<Card> cards = emptyManager.getCards(10, Category.MAP_NAME, Set.of(Tag.EUROPE));

        assertThat(cards).isEmpty();
    }

    @Test
    void testGetCards_NullSafety() {
        assertThatThrownBy(() -> manager.getCards(10, null, Set.of(Tag.EUROPE)))
            .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> manager.getCards(10, Category.MAP_NAME, null))
            .isInstanceOf(NullPointerException.class);
}
}