/* (C)2026 */
package com.worldquiz.controller;

import com.worldquiz.dto.RequestGame;
import com.worldquiz.entities.Game;
import com.worldquiz.entities.GameStat;
import com.worldquiz.service.GameService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/quiz")
@RequiredArgsConstructor
public class Controller {
    private final GameService gameService;

    @PostMapping
    public ResponseEntity<Game> createGame(@RequestBody RequestGame request) {
        int numberOfCards = request.number() != null ? request.number() : 1000000;
        Game game = gameService.createGame(request.categories(), request.tags(), numberOfCards);
        return ResponseEntity.ok(game);
    }

    @PostMapping("/{id}/finish")
    public ResponseEntity<GameStat> finishGame(@PathVariable("id") UUID id) {
        GameStat stat = gameService.finishGame(id);
        return ResponseEntity.ok(stat);
    }
}
