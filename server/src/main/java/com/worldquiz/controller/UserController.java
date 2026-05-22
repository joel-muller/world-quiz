/* (C)2026 */
package com.worldquiz.controller;

import com.worldquiz.dto.UserDto;
import com.worldquiz.entities.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    @GetMapping("/me")
    public ResponseEntity<UserDto> createGame(@AuthenticationPrincipal User user) {
        UserDto response = new UserDto(user.username(), user.email(), user.emailConfirmed());
        return ResponseEntity.ok(response);
    }
}
