/* (C)2026 */
package com.worldquiz.dto;

import lombok.Builder;

@Builder
public record TokenDto(String accessToken, String refreshToken, String tokenType) {
    public TokenDto {
        if (tokenType == null) {
            tokenType = "Bearer";
        }
    }
}
