/* (C)2026 */
package com.worldquiz.dto;

import jakarta.validation.constraints.*;

public record RegisterRequest(
        @NotNull @Size(min = 3, max = 30) @Pattern(regexp = "^[a-zA-Z0-9]+$") String username,
        @NotNull @Email String email,
        @NotNull @NotBlank String password) {}
