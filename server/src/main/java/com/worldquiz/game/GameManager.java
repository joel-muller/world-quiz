/* (C)2026 */
package com.worldquiz.game;

import com.worldquiz.entities.*;
import java.util.*;

public class GameManager {
    private final CardManager cardManager;
    private final Map<UUID, Game> games;

    public GameManager(CardManager cardManager) {
        this.cardManager = cardManager;
        this.games = new HashMap<>();
    }

    public Game createGame(List<Category> categories, List<Tag> tags, int maxNumberOfCards) {
        List<Card> cards = cardManager.getCards(maxNumberOfCards, categories, Set.copyOf(tags));
        Game game = new Game(UUID.randomUUID(), cards);
        games.put(game.id(), game);
        return game;
    }

    public GameStat finishGame(UUID id) {
        games.remove(id);
        return new GameStat(id, "Well done");
    }
}
