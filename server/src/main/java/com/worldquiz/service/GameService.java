/* (C)2026 */
package com.worldquiz.service;

import com.worldquiz.entities.*;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GameService {
    private final CardService cardService;
    private final Map<UUID, Game> games;

    public Game createGame(List<Category> categories, List<Tag> tags, int maxNumberOfCards) {
        List<Card> cards = cardService.getCards(maxNumberOfCards, categories, Set.copyOf(tags));
        Game game = new Game(UUID.randomUUID(), cards);
        games.put(game.id(), game);
        return game;
    }

    public GameStat finishGame(UUID id) {
        games.remove(id);
        return new GameStat(id, "Well done");
    }
}
