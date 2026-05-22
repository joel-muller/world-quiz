/* (C)2026 */
package com.worldquiz.controller;

import com.worldquiz.dto.CreateQuizRequest;
import com.worldquiz.dto.FinishGameRequest;
import com.worldquiz.dto.GameStatDto;
import com.worldquiz.dto.QuizDto;
import com.worldquiz.entities.User;
import com.worldquiz.service.GameService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/quiz")
@RequiredArgsConstructor
@Slf4j
public class GameController {
    private final GameService gameService;

    @PostMapping("/create")
    public ResponseEntity<QuizDto> createGame(
            @RequestBody CreateQuizRequest request, @AuthenticationPrincipal User user) {
        QuizDto quiz = gameService.createGame(request, user);
        return ResponseEntity.ok(quiz);
    }

    @PostMapping("/finish")
    public ResponseEntity<GameStatDto> finishGame(
            @RequestBody FinishGameRequest request, @AuthenticationPrincipal User user) {
        GameStatDto stat = gameService.finishGame(request, user);
        return ResponseEntity.ok(stat);
    }
}
