/* (C)2026 */
package com.worldquiz.controller;

import com.worldquiz.dto.AuthResponse;
import com.worldquiz.dto.LoginRequest;
import com.worldquiz.dto.RefreshRequest;
import com.worldquiz.dto.RegisterRequest;
import com.worldquiz.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestBody RefreshRequest request) {
        AuthResponse response = authService.refresh(request.refreshToken());
        return ResponseEntity.ok(response);
    }
}
