/* (C)2026 */
package com.worldquiz.controller;

import com.worldquiz.dto.CreateQuizRequest;
import com.worldquiz.dto.FinishGameRequest;
import com.worldquiz.dto.GameStatResponse;
import com.worldquiz.entities.Quiz;
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
    public ResponseEntity<Quiz> createGame(
            @RequestBody CreateQuizRequest request, @AuthenticationPrincipal User user) {
        Quiz quiz = gameService.createGame(request, user);
        return ResponseEntity.ok(quiz);
    }

    @PostMapping("/finish")
    public ResponseEntity<GameStatResponse> finishGame(@RequestBody FinishGameRequest request) {
        GameStatResponse stat = gameService.finishGame(request);
        return ResponseEntity.ok(stat);
    }
}
