/* (C)2026 */
package com.worldquiz.controller;

import com.worldquiz.dto.CreateQuizRequest;
import com.worldquiz.dto.FinishQuizRequest;
import com.worldquiz.dto.QuizDto;
import com.worldquiz.dto.QuizStatDto;
import com.worldquiz.entities.User;
import com.worldquiz.service.QuizService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/quiz")
@RequiredArgsConstructor
@Slf4j
public class QuizController {
    private final QuizService quizService;

    @PostMapping("/create")
    public ResponseEntity<QuizDto> createGame(
            @RequestBody CreateQuizRequest request, @AuthenticationPrincipal User user) {
        QuizDto quiz = quizService.createGame(request, user);
        return ResponseEntity.ok(quiz);
    }

    @PostMapping("/finish")
    public ResponseEntity<QuizStatDto> finishGame(
            @RequestBody FinishQuizRequest request, @AuthenticationPrincipal User user) {
        QuizStatDto stat = quizService.finishGame(request, user);
        return ResponseEntity.ok(stat);
    }
}
