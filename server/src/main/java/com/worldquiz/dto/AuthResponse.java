/* (C)2026 */
package com.worldquiz.dto;

import lombok.Builder;

@Builder
public record AuthResponse(String accessToken, String refreshToken, String tokenType) {
    public AuthResponse {
        if (tokenType == null) {
            tokenType = "Bearer";
        }
    }
}
