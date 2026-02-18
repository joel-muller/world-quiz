/* (C)2026 */
package com.worldquiz.controller;

import com.worldquiz.dto.RequestGame;
import com.worldquiz.entities.Game;
import com.worldquiz.entities.GameStat;
import com.worldquiz.entities.Place;
import com.worldquiz.game.CardManager;
import com.worldquiz.game.GameManager;
import com.worldquiz.reader.PlaceReader;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/quiz")
public class Controller {
    private final GameManager manager;

    public Controller() {
        PlaceReader reader = new PlaceReader("data", 10000);
        List<Place> places = reader.read();
        CardManager cardManager = new CardManager(places);
        this.manager = new GameManager(cardManager);
    }

    @PostMapping
    public ResponseEntity<Game> createGame(@RequestBody RequestGame request) {
        int numberOfCards = request.number() != null ? request.number() : 1000000;
        Game game = manager.createGame(request.categories(), request.tags(), numberOfCards);
        return ResponseEntity.ok(game);
    }

    @PostMapping("/{id}/finish")
    public ResponseEntity<GameStat> finishGame(@PathVariable("id") UUID id) {
        GameStat stat = manager.finishGame(id);
        return ResponseEntity.ok(stat);
    }
}
