/* (C)2026 */
package com.worldquiz.dto;

import com.worldquiz.entities.Category;

public record CardStatDto(
        String placeName, Category category, int guessedRight, int guessedWrong) {}
